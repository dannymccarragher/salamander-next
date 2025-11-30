import { useState } from 'react';
import { Alert, CircularProgress, Box } from '@mui/material';

// HOC for error handling and loading states
const withErrorHandling = (WrappedComponent) => {
    return function WithErrorHandlingComponent(props) {
        // Error and loading states
        const [error, setError] = useState(null);
        const [isLoading, setIsLoading] = useState(false);

        // Set error message
        const handleError = (error) => {
            setError(error.message || 'An error occurred');
        };

        // Toggle loading state
        const handleLoading = (loading) => {
            setIsLoading(loading);
        };

        // Clear error state
        const clearError = () => {
            setError(null);
        };

        // Show loading spinner
        if (isLoading) {
            return (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                    <CircularProgress />
                </Box>
            );
        }

        return (
            <>
                {/* Error alert */}
                {error && (
                    <Alert
                        severity="error"
                        onClose={clearError}
                        sx={{ mb: 2 }}
                    >
                        {error}
                    </Alert>
                )}
                {/* Wrapped component */}
                <WrappedComponent
                    {...props}
                    handleError={handleError}
                    handleLoading={handleLoading}
                    clearError={clearError}
                />
            </>
        );
    };
};

export default withErrorHandling; 