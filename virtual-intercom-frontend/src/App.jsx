import React, { useState, useEffect } from 'react';
import OwnerDashboard from './components/OwnerDashboard';
import GuestGate from './components/GuestGate';

function App() {
    // Stato globale condiviso dell'interfono
    const [callStatus, setCallStatus] = useState('LISTENING'); // LISTENING, RINGING, CONNECTED
    const [currentSessionId, setCurrentSessionId] = useState(null);

    useEffect(() => {
        // Configura il WebSocket centrale che aggiorna entrambi i pannelli contemporaneamente
        const socket = new WebSocket('ws://localhost:8090/ws/signaling');

        socket.onopen = () => console.log("✅ WebSocket unificato connesso!");

        socket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log("🎛️ App.jsx WebSocket Update:", data);

                if (data.event === "ring") {
                    setCallStatus("RINGING");
                    setCurrentSessionId(data.sessionId);
                } else if (data.event === "terminate") {
                    setCallStatus("LISTENING");
                    setCurrentSessionId(null);
                }
            } catch (err) {
                console.error("Errore nel parsing del messaggio globale", err);
            }
        };

        return () => socket.close();
    }, []);

    return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <h1>Smart Virtual Intercom System</h1>
            <hr />
            {/* Passiamo gli stati e le funzioni di aggiornamento ai componenti */}
            <OwnerDashboard
                callStatus={callStatus}
                setCallStatus={setCallStatus}
                sessionId={currentSessionId}
            />
            <hr />
            <GuestGate
                callStatus={callStatus}
                setCallStatus={setCallStatus}
                sessionId={currentSessionId}
                setSessionId={setCurrentSessionId}
            />
        </div>
    );
}

export default App;