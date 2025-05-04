import React, { useEffect, useState } from 'react';
import { Box, Button, Grid, Typography, Paper } from '@mui/material';
import { Seat } from '../types';
import { bookingService, screeningService } from '../services/api';

export interface SeatMapProps {
    screeningId: number;
    onSelectSeat: (seat: Seat | null) => void;
    selectedSeat: Seat | null;
}

export const SeatMap: React.FC<SeatMapProps> = ({ screeningId, onSelectSeat, selectedSeat }) => {
    const [seats, setSeats] = useState<Seat[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    // Group seats by row
    const seatsByRow = seats.reduce((acc, seat) => {
        if (!acc[seat.seatRow]) {
            acc[seat.seatRow] = [];
        }
        acc[seat.seatRow].push(seat);
        return acc;
    }, {} as Record<string, Seat[]>);

    const rows = Object.keys(seatsByRow).sort();

    const handleSeatClick = (seat: Seat) => {
        if (seat.status !== 'AVAILABLE' && seat.id !== selectedSeat?.id) {
            return;
        }

        // If clicking the same seat, deselect it
        if (selectedSeat?.id === seat.id) {
            onSelectSeat(null);
            return;
        }

        // Select the new seat
        onSelectSeat(seat);
    };

    // Find the maximum seat number
    const maxSeatNumber = Math.max(...seats.map(seat => parseInt(seat.seatNumber)));

    const getSeatStyles = (seat: Seat | null) => {
        const baseStyles = {
            width: 40,
            height: 40,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: seat?.status === 'AVAILABLE' ? 'pointer' : 'default',
            transition: 'all 0.2s ease',
            fontSize: '0.875rem',
            border: '2px solid',
            borderRadius: '4px',
            backgroundColor: 'transparent',
            '&:hover': {
                transform: seat?.status === 'AVAILABLE' ? 'scale(1.05)' : 'none',
            }
        };

        if (!seat) {
            return {
                ...baseStyles,
                borderColor: '#e0e0e0',
                color: '#bdbdbd',
                backgroundColor: '#f5f5f5',
                cursor: 'default',
            };
        }

        if (selectedSeat?.id === seat.id) {
            return {
                ...baseStyles,
                backgroundColor: '#1976d2',
                color: 'white',
                borderColor: '#1976d2',
            };
        }

        switch (seat.status) {
            case 'AVAILABLE':
                return {
                    ...baseStyles,
                    borderColor: '#2e7d32',
                    color: '#2e7d32',
                    '&:hover': {
                        ...baseStyles['&:hover'],
                        backgroundColor: '#2e7d3220',
                    }
                };
            case 'BOOKED':
                return {
                    ...baseStyles,
                    borderColor: '#d32f2f',
                    color: '#d32f2f',
                    backgroundColor: '#d32f2f20',
                };
            case 'RESERVED':
                return {
                    ...baseStyles,
                    borderColor: '#ed6c02',
                    color: '#ed6c02',
                    backgroundColor: '#ed6c0220',
                };
            default:
                return baseStyles;
        }
    };

    // Poll for seat updates
    useEffect(() => {
        const fetchSeats = async () => {
            try {
                const response = await screeningService.getAvailableSeats(screeningId);
                setSeats(response.data);

                // If our selected seat is no longer available, deselect it
                if (selectedSeat) {
                    const updatedSeat = response.data.find(s => s.id === selectedSeat.id);
                    if (updatedSeat?.status !== 'AVAILABLE' && updatedSeat?.id !== selectedSeat.id) {
                        onSelectSeat(null);
                    }
                }
            } catch (err) {
                console.error('Error fetching seats:', err);
                setError('Failed to load seats');
            }
        };

        fetchSeats();
        const interval = setInterval(fetchSeats, 5000); // Poll every 5 seconds

        return () => {
            clearInterval(interval);
        };
    }, [screeningId, selectedSeat, onSelectSeat]);

    if (!seats.length) {
        return (
            <Box sx={{ p: 3 }}>
                <Typography color="error" align="center">
                    No seats available for this screening.
                </Typography>
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Paper
                elevation={3}
                sx={{
                    p: 4,
                    backgroundColor: '#f5f5f5',
                    borderRadius: 2
                }}
            >
                <Typography variant="h6" gutterBottom align="center">
                    Screen
                </Typography>
                <Box
                    sx={{
                        width: '100%',
                        height: 8,
                        bgcolor: '#1976d2',
                        mb: 6,
                        borderRadius: 1,
                        boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
                    }}
                />

                {error && (
                    <Typography color="error" align="center" gutterBottom>
                        {error}
                    </Typography>
                )}

                <Grid container spacing={2}>
                    {rows.map(row => (
                        <Grid item xs={12} key={`row-${row}`}>
                            <Box display="flex" justifyContent="center" alignItems="center" gap={1}>
                                <Typography
                                    variant="body1"
                                    sx={{
                                        minWidth: 30,
                                        fontWeight: 'bold',
                                        color: '#666'
                                    }}
                                >
                                    {row}
                                </Typography>
                                <Box display="flex" gap={1}>
                                    {Array.from({ length: maxSeatNumber }).map((_, index) => {
                                        const seatNumber = (index + 1).toString();
                                        const seat = seatsByRow[row].find(s => s.seatNumber === seatNumber) || null;

                                        return (
                                            <Box
                                                key={`seat-${row}-${seatNumber}`}
                                                onClick={() => seat && handleSeatClick(seat)}
                                                sx={getSeatStyles(seat)}
                                            >
                                                {seatNumber}
                                            </Box>
                                        );
                                    })}
                                </Box>
                            </Box>
                        </Grid>
                    ))}
                </Grid>

                <Box sx={{ mt: 6, display: 'flex', gap: 4, justifyContent: 'center' }}>
                    <Box display="flex" alignItems="center" gap={1}>
                        <Box sx={{
                            width: 24,
                            height: 24,
                            border: '2px solid #2e7d32',
                            borderRadius: 0.5
                        }} />
                        <Typography variant="body2" color="#666">Available</Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={1}>
                        <Box sx={{
                            width: 24,
                            height: 24,
                            border: '2px solid #d32f2f',
                            borderRadius: 0.5,
                            bgcolor: '#d32f2f20'
                        }} />
                        <Typography variant="body2" color="#666">Booked</Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={1}>
                        <Box sx={{
                            width: 24,
                            height: 24,
                            border: '2px solid #ed6c02',
                            borderRadius: 0.5,
                            bgcolor: '#ed6c0220'
                        }} />
                        <Typography variant="body2" color="#666">Reserved</Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={1}>
                        <Box sx={{
                            width: 24,
                            height: 24,
                            bgcolor: '#1976d2',
                            border: '2px solid #1976d2',
                            borderRadius: 0.5,
                        }} />
                        <Typography variant="body2" color="#666">Selected</Typography>
                    </Box>
                </Box>
            </Paper>
        </Box>
    );
}; 