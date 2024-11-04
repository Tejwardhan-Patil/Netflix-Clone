import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { getRecommendations } from '../services/api';

const RecommendationList = ({ userId, onRecommendationClick }) => {
    const [recommendations, setRecommendations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRecommendations = async () => {
            try {
                const data = await getRecommendations(userId);
                setRecommendations(data);
            } catch (err) {
                setError('Failed to load recommendations.');
            } finally {
                setLoading(false);
            }
        };

        fetchRecommendations();
    }, [userId]);

    if (loading) {
        return <div style={styles.loading}>Loading recommendations...</div>;
    }

    if (error) {
        return <div style={styles.error}>{error}</div>;
    }

    return (
        <div style={styles.container}>
            <h2 style={styles.header}>Recommended for You</h2>
            <ul style={styles.list}>
                {recommendations.map((rec) => (
                    <li
                        key={rec.id}
                        style={styles.listItem}
                        onClick={() => onRecommendationClick(rec.id)}
                    >
                        <img
                            src={rec.thumbnailUrl}
                            alt={rec.title}
                            style={styles.thumbnail}
                        />
                        <div style={styles.info}>
                            <h3 style={styles.title}>{rec.title}</h3>
                            <p style={styles.description}>{rec.description}</p>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

RecommendationList.propTypes = {
    userId: PropTypes.string.isRequired,
    onRecommendationClick: PropTypes.func.isRequired,
};

const styles = {
    container: {
        padding: '20px',
        backgroundColor: '#f8f9fa',
    },
    header: {
        fontSize: '24px',
        marginBottom: '10px',
    },
    list: {
        listStyleType: 'none',
        padding: 0,
        display: 'flex',
        flexWrap: 'wrap',
    },
    listItem: {
        width: '200px',
        margin: '10px',
        padding: '10px',
        backgroundColor: '#fff',
        borderRadius: '5px',
        boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        cursor: 'pointer',
        transition: 'transform 0.3s',
    },
    listItemHover: {
        transform: 'scale(1.05)',
    },
    thumbnail: {
        width: '100%',
        borderRadius: '4px',
        marginBottom: '10px',
    },
    info: {
        textAlign: 'left',
    },
    title: {
        fontSize: '18px',
    },
    description: {
        color: '#6c757d',
        fontSize: '14px',
    },
    loading: {
        fontSize: '18px',
        color: '#007bff',
    },
    error: {
        fontSize: '18px',
        color: '#dc3545',
    },
};

export default RecommendationList;

/**
 * API Service (api.js)
 * This file manages API calls and interacts with the backend services.
 */

import axios from 'axios';

export const getRecommendations = async (userId) => {
    const response = await axios.get(`/api/recommendations/${userId}`);
    return response.data;
};

/**
 * Test cases for RecommendationList.js (RecommendationList.test.js)
 */

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import RecommendationList from './RecommendationList';

jest.mock('../services/api', () => ({
    getRecommendations: jest.fn(),
}));

describe('RecommendationList', () => {
    const mockRecommendations = [
        { id: '1', title: 'Movie 1', description: 'Description 1', thumbnailUrl: 'https://website.com/img1.jpg' },
        { id: '2', title: 'Movie 2', description: 'Description 2', thumbnailUrl: 'https://website.com/img2.jpg' },
    ];

    const mockApi = require('../services/api');

    beforeEach(() => {
        mockApi.getRecommendations.mockResolvedValue(mockRecommendations);
    });

    it('renders recommendations after loading', async () => {
        render(<RecommendationList userId="user1" onRecommendationClick={jest.fn()} />);
        expect(screen.getByText(/loading recommendations/i)).toBeInTheDocument();

        const items = await screen.findAllByRole('listitem');
        expect(items).toHaveLength(mockRecommendations.length);
    });

    it('displays an error message if the API call fails', async () => {
        mockApi.getRecommendations.mockRejectedValue(new Error('API Error'));
        render(<RecommendationList userId="user1" onRecommendationClick={jest.fn()} />);
        const errorMessage = await screen.findByText(/failed to load recommendations/i);
        expect(errorMessage).toBeInTheDocument();
    });

    it('calls onRecommendationClick when an item is clicked', async () => {
        const onRecommendationClick = jest.fn();
        render(<RecommendationList userId="user1" onRecommendationClick={onRecommendationClick} />);

        const items = await screen.findAllByRole('listitem');
        fireEvent.click(items[0]);

        expect(onRecommendationClick).toHaveBeenCalledWith('1');
    });
});