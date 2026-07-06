import React from 'react';
import GuestGate from './components/GuestGate';
import OwnerDashboard from './components/OwnerDashboard';

/**
 * Main Application Hub showcasing both interfaces for testing purposes.
 */
function App() {
  return (
    <div>
      <OwnerDashboard />
      <hr />
      <GuestGate />
    </div>
  );
}

export default App;