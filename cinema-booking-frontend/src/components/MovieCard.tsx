import React from 'react';
import { Card, CardContent, CardMedia, Typography, Button } from '@mui/material';
import { Movie } from '../types';

interface MovieCardProps {
    movie: Movie;
    onSelect: (movie: Movie) => void;
}

export const MovieCard: React.FC<MovieCardProps> = ({ movie, onSelect }) => {
    return (
        <Card sx={{ maxWidth: 345, margin: 2 }}>
            <CardMedia
                component="img"
                height="400"
                image={movie.imageUrl}
                alt={movie.title}
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                    {movie.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    {movie.description}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Duration: {movie.duration} minutes
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ mt: 2 }}
                    onClick={() => onSelect(movie)}
                >
                    Select Movie
                </Button>
            </CardContent>
        </Card>
    );
}; 