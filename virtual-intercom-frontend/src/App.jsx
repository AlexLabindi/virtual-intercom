import React, { useState, useEffect } from 'react';
import OwnerDashboard from './components/OwnerDashboard';
import GuestGate from './components/GuestGate';

function App() {
    const [callStatus, setCallStatus] = useState('LISTENING'); // LISTENING, RINGING, CONNECTED
    const [sessionId, setSessionId] = useState(null);
    const [messages, setMessages] = useState([]); // Lista dei messaggi della chat
    const [socket, setSocket] = useState(null);

    useEffect(() => {
        const ws = new WebSocket('ws://localhost:8090/ws/signaling');

        ws.onopen = () => {
            console.log("✅ Intercom connected to the reporting server");
            setSocket(ws);
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log("📩 WebSocket event received:", data);

                if (data.event === "ring") {
                    setCallStatus("RINGING");
                    // Gestiamo sia se arriva come sessionId che come id
                    const id = data.sessionId || data.id;
                    setSessionId(id);
                    setMessages([]);
                } else if (data.event === "accept") {
                    setCallStatus("CONNECTED");
                } else if (data.event === "terminate") {
                    setCallStatus("LISTENING");
                    setSessionId(null);
                    setMessages([]);
                } else if (data.event === "chat") {
                    setMessages((prev) => [...prev, { sender: data.sender, text: data.text }]);
                }
            } catch (err) {
                console.error("WebSocket decoding error:", err);
            }
        };
        return () => ws.close();
    }, []);

    // Funzione per inviare messaggi di chat tramite il WebSocket
    const sendChatMessage = (sender, text) => {
        if (socket && socket.readyState === WebSocket.OPEN) {
            const payload = JSON.stringify({
                event: "chat",
                sessionId: sessionId,
                sender: sender,
                text: text
            });
            socket.send(payload);
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Segoe UI, sans-serif', backgroundColor: '#1a1a1a', color: '#fff', minHeight: '100vh' }}>
            <h1 style={{ textAlign: 'center', color: '#4CAF50' }}>Smart Intercom System 2.0</h1>
            <div style={{ display: 'flex', gap: '40px', justifyContent: 'center', marginTop: '30px' }}>

                {/* Pannello Proprietario */}
                <OwnerDashboard
                    callStatus={callStatus}
                    sessionId={sessionId}
                    messages={messages}
                    onSendMessage={(txt) => sendChatMessage('OWNER', txt)}
                />

                {/* Pannello Cancello */}
                <GuestGate
                    callStatus={callStatus}
                    sessionId={sessionId}
                    setSessionId={setSessionId}
                    setCallStatus={setCallStatus}
                    messages={messages}
                    onSendMessage={(txt) => sendChatMessage('GUEST', txt)}
                />

            </div>
        </div>
    );
}

export default App;