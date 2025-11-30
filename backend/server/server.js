import express from 'express';
import router from './router/router.js';
import dotenv from 'dotenv';
import cors from "cors";

dotenv.config({ path: '../.env' });

const app = express();
const PORT = 3000;

app.use(cors());

app.use('/', router);

app.use('/process', express.static('/results'));

app.listen(PORT, () => console.log(`Listening on http://localhost:${PORT}`));