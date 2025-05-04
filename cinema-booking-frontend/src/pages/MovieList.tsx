import React, { useEffect, useState } from 'react';
import { Container, Grid, Typography, Box, Button } from '@mui/material';
import { MovieCard } from '../components/MovieCard';
import { movieService } from '../services/api';
import { Movie } from '../types';
import { useNavigate } from 'react-router-dom';

export const MovieList: React.FC = () => {
    const [movies, setMovies] = useState<Movie[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMovies = async () => {
            try {
                const response = await movieService.getAllMovies();
                setMovies(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching movies:', error);
                setError('Failed to load movies. Please try again later.');
                setLoading(false);
            }
        };

        fetchMovies();
    }, []);

    const handleSelectMovie = (movie: Movie) => {
        navigate(`/movie/${movie.id}/screenings`);
    };

    if (loading) {
        return (
            <Container>
                <Typography>Loading...</Typography>
            </Container>
        );
    }

    if (error) {
        return (
            <Container>
                <Typography color="error">{error}</Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
                <Typography variant="h4" component="h1">
                    Available Movies
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => navigate('/booking-history')}
                >
                    View My Bookings
                </Button>
            </Box>

            <Grid container spacing={3} justifyContent="center">
                {movies.map((movie) => (
                    <Grid item key={movie.id} xs={12} sm={6} md={4}>
                        <MovieCard movie={movie} onSelect={handleSelectMovie} />
                    </Grid>
                ))}
            </Grid>
        </Container>
    );
}; 