import React, { useEffect, useState } from 'react';
import { Container, Typography, Card, CardContent, Button, Box, Grid, CircularProgress } from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { Movie, Screening } from '../types';
import { movieService } from '../services/api';

export const ScreeningList: React.FC = () => {
    const { movieId } = useParams<{ movieId: string }>();
    const navigate = useNavigate();
    const [movie, setMovie] = useState<Movie | null>(null);
    const [screenings, setScreenings] = useState<Screening[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>('');

    const fetchScreenings = async () => {
        try {
            if (!movieId) return;
            const screeningsResponse = await movieService.getMovieScreenings(Number(movieId));
            setScreenings(screeningsResponse.data);
        } catch (error) {
            console.error('Error fetching screenings:', error);
            setError('Failed to load screenings. Please try again later.');
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (!movieId) return;

                const [movieResponse, screeningsResponse] = await Promise.all([
                    movieService.getMovieById(Number(movieId)),
                    movieService.getMovieScreenings(Number(movieId))
                ]);

                setMovie(movieResponse.data);
                setScreenings(screeningsResponse.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching data:', error);
                setError('Failed to load screenings. Please try again later.');
                setLoading(false);
            }
        };

        fetchData();

        // Set up polling for screenings
        const interval = setInterval(fetchScreenings, 5000); // Poll every 5 seconds

        return () => {
            clearInterval(interval); // Cleanup on unmount
        };
    }, [movieId]);

    const handleSelectScreening = (screening: Screening) => {
        navigate(`/movie/${movieId}/screening/${screening.id}/seats`);
    };

    if (loading) {
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
                <CircularProgress />
            </Container>
        );
    }

    if (error || !movie) {
        return (
            <Container>
                <Typography color="error" align="center">
                    {error || 'Movie not found'}
                </Typography>
                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                    <Button variant="contained" onClick={() => navigate('/')}>
                        Back to Movies
                    </Button>
                </Box>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom align="center">
                {movie.title}
            </Typography>

            <Box sx={{ mb: 4 }}>
                <Typography variant="body1" align="center" color="text.secondary">
                    Duration: {movie.duration} minutes
                </Typography>
                <Typography variant="body1" align="center" color="text.secondary">
                    Director: {movie.director}
                </Typography>
                <Typography variant="body1" align="center" color="text.secondary">
                    Genre: {movie.genre}
                </Typography>
            </Box>

            <Typography variant="h5" gutterBottom align="center" sx={{ mb: 4 }}>
                Available Screenings
            </Typography>

            {screenings.length === 0 ? (
                <Typography align="center" color="text.secondary">
                    No screenings available for this movie.
                </Typography>
            ) : (
                <Grid container spacing={3} justifyContent="center">
                    {screenings.map((screening) => (
                        <Grid item key={screening.id} xs={12} sm={6} md={4}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        {new Date(screening.screeningTime).toLocaleDateString()}
                                    </Typography>
                                    <Typography variant="body1" color="text.secondary" gutterBottom>
                                        Time: {new Date(screening.screeningTime).toLocaleTimeString()}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        Available Seats: {screening.availableSeats} / {screening.totalSeats}
                                    </Typography>
                                    <Button
                                        variant="contained"
                                        fullWidth
                                        sx={{ mt: 2 }}
                                        onClick={() => handleSelectScreening(screening)}
                                        disabled={screening.availableSeats === 0}
                                    >
                                        {screening.availableSeats === 0 ? 'Sold Out' : 'Select Seats'}
                                    </Button>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            <Box sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
                <Button variant="outlined" onClick={() => navigate('/')}>
                    Back to Movies
                </Button>
            </Box>
        </Container>
    );
}; 