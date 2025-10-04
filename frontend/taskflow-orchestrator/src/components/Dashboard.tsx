// pages/Dashboard.tsx (example usage)
import React from 'react';
import Layout from './layouts/Layout.tsx';
import { TaskCard } from './TaskCard.tsx';

const Dashboard: React.FC = () => {
    return (
        <Layout>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <TaskCard title="Implement Login System" status="in-progress" />
                <TaskCard title="Design Database Schema" status="completed" />
                <TaskCard title="Write API Documentation" status="in-progress" />
                {/* Add more cards as needed */}
            </div>
        </Layout>
    );
};

export default Dashboard;