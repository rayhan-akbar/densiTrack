// Import necessary modules
const express = require("express");
const bodyParser = require("body-parser");
const session = require("express-session");
const cors = require("cors");
const passport = require("passport");
const { Client } = require("pg");

// Database setup
const db = new Client({
    user: 'aflahgmc',
    host: 'ep-broad-pine-502933.ap-southeast-1.aws.neon.tech',
    database: 'Densi',
    password: 'uZtHEC7e3yIo',
    port: 5432,
    sslmode: 'require',
    ssl: true
});

db.connect((err) => {
    if (err) {
        console.log(err);
    } else {
        console.log('Successfully connected to the database');
    }
});

// Express app setup
const port = process.env.PORT || 3000;
const app = express();
const store = new session.MemoryStore();

const corsOptions = {
    origin: "*",  // Allow all origins for development. Change this for production to restrict to frontend domain.
    credentials: true,
    optionsSuccessStatus: 200,
};

app.use(express.static("public"));

app.get("/", (req, res) => {
    res.sendFile("index.html");
});

app.use(
    session({
        secret: "secret",
        resave: false,
        cookie: { maxAge: 6000000 },
        saveUninitialized: false,
        store,
    })
);

app.use(passport.initialize());
app.use(passport.session());

app.use(cors(corsOptions));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Service logic

// Service logic

async function getObject() {
    const query = `SELECT front, back FROM bus ORDER BY ctid DESC LIMIT 1`;  // Fetch front and back from the last row
    const result = await db.query(query);
    
    if (result.rowCount === 1) {
        return {
            message: "Last front and back values found",
            front: result.rows[0].front,  // Return the front column
            back: result.rows[0].back     // Return the back column
        };
    } else {
        return {
            message: "No data found",
        };
    }
}

// Route setup for GET request to fetch the last front and back values
app.get("/tr/getobject", async (req, res) => {
    try {
        const result = await getObject();  // Call the updated function
        res.json(result);  // Respond with the result
    } catch (err) {
        res.status(500).json(err);  // Handle errors gracefully
    }
});

// Server start
app.listen(port, () => {
    console.log("Server is running on port: " + port);
});
