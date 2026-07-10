import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import OwnerDashboard from './components/OwnerDashboard';
import GuestGate from './components/GuestGate';

function App() {
  const [callStatus, setCallStatus] = useState('LISTENING');
  const [sessionId, setSessionId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);

  // Cattura l'IP reale della macchina su cui gira il browser (non più localhost fisso)
  const HOST = window.location.hostname;

  useEffect(() => {
    // Il WebSocket ora si connette dinamicamente all'IP del PC
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
      <div style={{ padding: '20px', fontFamily: 'Segoe UI, sans-serif', backgroundColor: '#1a1a1a', color: '#fff', minHeight: '100vh' }}>
        
        {/* Menu di Navigazione provvisorio per muoversi agevolmente */}
        <nav style={{ textAlign: 'center', marginBottom: '30px', padding: '10px', background: '#333', borderRadius: '8px' }}>
          <Link to="/" style={{ color: '#ff9800', marginRight: '20px', textDecoration: 'none', fontWeight: 'bold' }}>🚪 Vai al Cancello (Guest)</Link>
          <Link to="/owner" style={{ color: '#4CAF50', textDecoration: 'none', fontWeight: 'bold' }}>🏠 Vai alla Dashboard (Owner)</Link>
        </nav>

        <div style={{ display: 'flex', justifyContent: 'center' }}>
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
                host={HOST} // Passiamo l'IP al componente
              />
            } />

            {/* PAGINA 2: La Dashboard Privata */}
            <Route path="/owner" element={
              <OwnerDashboard 
                callStatus={callStatus} 
                sessionId={sessionId} 
                messages={messages}
                onSendMessage={(txt) => sendChatMessage('OWNER', txt)}
                host={HOST} // Passiamo l'IP al componente
              />
            } />
          </Routes>
        </div>

      </div>
    </Router>
  );
}

export default App;