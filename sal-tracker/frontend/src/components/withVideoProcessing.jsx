import { useState } from 'react';
import { Alert } from '@mui/material';

// HOC for video processing
const withVideoProcessing = (WrappedComponent) => {
    return function WithVideoProcessingComponent(props) {
        // States for processing
        const [status, setStatus] = useState("idle");
        const [error, setError] = useState("");
        const [jobId, setJobId] = useState(null);

        // Start video processing
        const start = async (filename, color, threshold) => {
            setError("");
            setStatus("processing");

            try {
                // Call backend API
                const res = await fetch(
                    `http://localhost:3000/process/${filename}?targetColor=${color.slice(1)}&threshold=${threshold}`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    }
                );

                // Handle error response
                if (!res.ok) {
                    const { error } = await res.json();
                    throw new Error(error);
                }

                const data = await res.json();
                setJobId(data.jobId);
            } catch (err) {
                setError(err.message);
                setStatus("idle");
            }
        };

        return (
            <>
                {/* Error alert */}
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}
                {/* Wrapped component */}
                <WrappedComponent
                    {...props}
                    status={status}
                    error={error}
                    jobId={jobId}
                    start={start}
                />
            </>
        );
    };
};

export default withVideoProcessing; 