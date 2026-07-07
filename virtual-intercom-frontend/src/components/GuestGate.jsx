import React, { useState } from 'react';

function GuestGate({ callStatus, sessionId, setSessionId, setCallStatus, messages, onSendMessage }) {
    const [typedMsg, setTypedMsg] = useState('');

    const handleRing = async () => {
        try {
            const response = await fetch('http://localhost:8090/api/intercom/ring', { method: 'POST' });
            if (response.ok) {
                const session = await response.json();
                console.log("🔔 Risposta DTO dal Server:", session);

                // Mappiamo l'id restituito dal controller (può essere id o sessionId a seconda del DTO)
                const activeId = session.id || session.sessionId;

                setSessionId(activeId);
                setCallStatus('RINGING');
            }
        } catch (err) {
            console.error("Errore ring:", err);
        }
    };

    const handleCancel = async () => {
        if (!sessionId) return console.error("Impossibile cancellare: sessionId mancante!");
        try {
            await fetch(`http://localhost:8090/api/intercom/calls/${sessionId}/terminate?reason=CANCELED`, { method: 'POST' });
        } catch (err) {
            console.error("Errore chiusura:", err);
        }
    };

    const send = (e) => {
        e.preventDefault();
        if (!typedMsg.trim()) return;
        onSendMessage(typedMsg);
        setTypedMsg('');
    };

    return (
        <div style={{ border: '2px solid #ff9800', padding: '20px', borderRadius: '8px', width: '350px', background: '#2a2a2a' }}>
            <h2>🔔 Smart Intercom Gate</h2>

            {callStatus === 'LISTENING' && (
                <button onClick={handleRing} style={{ backgroundColor: '#ff9800', color: 'black', fontWeight: 'bold', padding: '15px', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%', marginTop: '20px' }}>🔔 Press to Ring</button>
            )}

            {callStatus === 'RINGING' && (
                <div style={{ marginTop: '20px', textAlign: 'center' }}>
                    <p style={{ color: '#ff9800' }}>🔔 Ringing... Waiting for answer...</p>
                    <button onClick={handleCancel} style={{ backgroundColor: 'red', color: 'white', padding: '8px', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%', marginTop: '10px' }}>Cancel</button>
                </div>
            )}

            {callStatus === 'CONNECTED' && (
                <div style={{ marginTop: '20px' }}>
                    <div style={{ height: '150px', overflowY: 'auto', border: '1px solid #555', padding: '10px', background: '#1e1e1e', borderRadius: '4px' }}>
                        {messages.map((m, i) => (
                            <p key={i} style={{ margin: '5px 0', color: m.sender === 'GUEST' ? '#ff9800' : '#4CAF50' }}>
                                <strong>{m.sender}:</strong> {m.text}
                            </p>
                        ))}
                    </div>
                    <form onSubmit={send} style={{ display: 'flex', marginTop: '10px', gap: '5px' }}>
                        <input type="text" value={typedMsg} onChange={(e) => setTypedMsg(e.target.value)} style={{ flex: 1, padding: '5px' }} placeholder="Parla con il proprietario..." />
                        <button type="submit" style={{ padding: '5px 10px' }}>Invia</button>
                    </form>
                    <button onClick={handleCancel} style={{ backgroundColor: 'red', color: 'white', padding: '8px', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%', marginTop: '10px' }}>Hang Up</button>
                </div>
            )}
        </div>
    );
}

export default GuestGate;