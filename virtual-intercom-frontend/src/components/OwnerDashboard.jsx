import React, { useState } from 'react';

function OwnerDashboard({ callStatus, sessionId, messages, onSendMessage }) {
    const [typedMsg, setTypedMsg] = useState('');

    const handleAccept = async () => {
        if (!sessionId) return console.error("Unable to accept: Missing sessionId!");
        try {
            await fetch(`http://localhost:8090/api/intercom/calls/${sessionId}/accept`, { method: 'POST' });
        } catch (err) {
            console.error("Error while accepting:", err);
        }
    };

    const handleReject = async () => {
        if (!sessionId) return console.error("Unable to refuse: Missing sessionId!");
        try {
            await fetch(`http://localhost:8090/api/intercom/calls/${sessionId}/terminate?reason=REJECTED`, { method: 'POST' });
        } catch (err) {
            console.error("Error while terminating:", err);
        }
    };

    const send = (e) => {
        e.preventDefault();
        if (!typedMsg.trim()) return;
        onSendMessage(typedMsg);
        setTypedMsg('');
    };

    return (
        <div style={{ border: '2px solid #4CAF50', padding: '20px', borderRadius: '8px', width: '350px', background: '#2a2a2a' }}>
            <h2>🏠 Owner Home Dashboard</h2>
            <p>Status: <strong>{callStatus}</strong></p>

            {callStatus === 'RINGING' && (
                <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                    <button onClick={handleAccept} style={{ backgroundColor: 'green', color: 'white', padding: '10px', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Accept</button>
                    <button onClick={handleReject} style={{ backgroundColor: 'red', color: 'white', padding: '10px', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>Reject</button>
                </div>
            )}

            {callStatus === 'CONNECTED' && (
                <div style={{ marginTop: '20px' }}>
                    <div style={{ height: '150px', overflowY: 'auto', border: '1px solid #555', padding: '10px', background: '#1e1e1e', borderRadius: '4px' }}>
                        {messages.map((m, i) => (
                            <p key={i} style={{ margin: '5px 0', color: m.sender === 'OWNER' ? '#4CAF50' : '#ff9800' }}>
                                <strong>{m.sender}:</strong> {m.text}
                            </p>
                        ))}
                    </div>
                    <form onSubmit={send} style={{ display: 'flex', marginTop: '10px', gap: '5px' }}>
                        <input type="text" value={typedMsg} onChange={(e) => setTypedMsg(e.target.value)} style={{ flex: 1, padding: '5px' }} placeholder="Write at the gate..." />
                        <button type="submit" style={{ padding: '5px 10px' }}>Invia</button>
                    </form>
                    <button onClick={handleReject} style={{ backgroundColor: 'red', color: 'white', padding: '8px', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%', marginTop: '10px' }}>Terminate Call</button>
                </div>
            )}
        </div>
    );
}

export default OwnerDashboard;