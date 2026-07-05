import React, { useState } from 'react';

/**
 * GuestGate Component - Rendered on the visitor's smartphone via the Captive Portal.
 * Handles the ringing action and manages WebRTC audio streaming states.
 */
export default function GuestGate() {
    const [status, setStatus] = useState('IDLE'); // IDLE, RINGING, CONNECTED, TERMINATED
    const [error, setError] = useState(null);

    const handleRing = async () => {
        setStatus('RINGING');
        setError(null);

        try {
            // Trigger the backend API protected by Resilience4j Rate Limiting
            const response = await fetch('http://localhost:8080/api/v1/intercom/ring', {
                method: 'POST'
            });

            if (response.status === 429) {
                const errData = await response.json();
                throw new Error(errData.message);
            }

            if (!response.ok) throw new Error('Failed to reach the intercom system.');

            const data = await response.json();
            console.log('Session initialized successfully:', data.sessionId);
            // Here the app waits for the WebSocket signaling to upgrade to WebRTC audio
        } catch (err) {
            setError(err.message);
            setStatus('IDLE');
        }
    };

    return (
        <div style={{ textAlign: 'center', padding: '50px' }}>
            <h1>Smart Intercom Gate</h1>
            {status === 'IDLE' && (
                <button onClick={handleRing} style={{ padding: '20px 40px', fontSize: '20px' }}>
                    🔔 Press to Ring
                </button>
            )}
            {status === 'RINGING' && <p>🔔 Ringing... Waiting for the owner to answer.</p>}
            {error && <p style={{ color: 'red', marginTop: '20px' }}>⚠️ {error}</p>}
        </div>
    );
}