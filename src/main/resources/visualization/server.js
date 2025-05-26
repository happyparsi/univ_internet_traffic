const express = require('express');
const fs = require('fs').promises;
const path = require('path');

const app = express();
const port = 8080;

app.use(express.json());
app.use(express.static(__dirname));

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.get('/data.json', async (req, res) => {
    try {
        const data = await fs.readFile('data.json', 'utf8');
        res.json(JSON.parse(data));
    } catch (err) {
        res.status(500).json({ error: 'Failed to read data.json' });
    }
});

app.get('/userGraphData.json', async (req, res) => {
    try {
        const data = await fs.readFile('userGraphData.json', 'utf8');
        res.json(JSON.parse(data));
    } catch (err) {
        res.status(500).json({ error: 'Failed to read userGraphData.json' });
    }
});

app.post('/saveUserGraphData', async (req, res) => {
    try {
        await fs.writeFile('userGraphData.json', JSON.stringify(req.body, null, 2));
        res.json({ success: true });
    } catch (err) {
        res.status(500).json({ error: 'Failed to save userGraphData.json' });
    }
});

app.post('/saveGraphData', async (req, res) => {
    try {
        await fs.writeFile('data.json', JSON.stringify(req.body, null, 2));
        res.json({ success: true });
    } catch (err) {
        res.status(500).json({ error: 'Failed to save data.json' });
    }
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});