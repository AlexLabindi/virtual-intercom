import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import OwnerDashboard from './components/OwnerDashboard';
import GuestGate from './components/GuestGate';

function App() {
  const [callStatus, setCallStatus] = useState('LISTENING');
  const [sessionId, setSessionId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);

  const HOST = window.location.hostname;

  useEffect(() => {
    const ws = new WebSocket(`ws://${HOST}:8090/ws/signaling`);

    ws.onopen = () => {
      console.log(`✅ Connesso al server WebSocket su ${HOST}`);
      setSocket(ws);
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.event === "ring") {
          setCallStatus("RINGING");
          setSessionId(data.sessionId || data.id);
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
        console.error("Errore WebSocket:", err);
      }
    };

    return () => ws.close();
  }, [HOST]);

  const sendChatMessage = (sender, text) => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({ event: "chat", sessionId, sender, text }));
    }
  };

  return (
      <Router>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#1a1a1a', color: '#fff', padding: '20px', fontFamily: 'Segoe UI, sans-serif' }}>
          <Routes>
            {/* PAGINA 1: Il Cancello Pubblico */}
            <Route path="/" element={
              <GuestGate
                  callStatus={callStatus}
                  sessionId={sessionId}
                  setSessionId={setSessionId}
                  setCallStatus={setCallStatus}
                  messages={messages}
                  onSendMessage={(txt) => sendChatMessage('SMART', txt)}
                  host={HOST}
              />
            } />

            {/* PAGINA 2: La Dashboard Privata */}
            <Route path="/owner" element={
              <OwnerDashboard
                  callStatus={callStatus}
                  sessionId={sessionId}
                  setSessionId={setSessionId} // Passato per sbloccare l'Owner
                  setCallStatus={setCallStatus}   // Passato per sbloccare l'Owner
                  messages={messages}
                  onSendMessage={(txt) => sendChatMessage('OWNER', txt)}
                  host={HOST}
              />
            } />
          </Routes>
        </div>
      </Router>
  );
}

export default App;