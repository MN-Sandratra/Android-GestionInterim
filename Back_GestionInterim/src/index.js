const express = require('express');
const bodyParser = require('body-parser')
const { default: mongoose } = require('mongoose');
require('dotenv').config();

const dblink=process.env.CONNECTION_DB;
console.log(dblink);
const app = express();
const port = 8000;

const JobSeekerRouter =require("./routes/JobSeeker");
const EmployerRouter =require("./routes/Employer");
const SubscriptionRouter =require("./routes/Subscription");

app.use(bodyParser.urlencoded({ extended: false }))
// parse application/json
app.use(bodyParser.json())
app.use(function (req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', '*');
  next();
});

app.use('/api/joobseekers',JobSeekerRouter);
app.use('/api/employers',EmployerRouter);
app.use('/api/subscriptions',SubscriptionRouter);

app.get('/', (req, res) => {
    res.send('Hello, World!');
  });
  
  
  async function startServer(){
    await mongoose.connect(
      ""+dblink,
    ).then(()=>console.log("Connected to MongoDb Database"))
    .catch(()=>console.log("Connection Failed"));
    app.listen(port, () => {
      console.log(`Server running at http://localhost:${port}`);
    });
  }
  
  startServer();
  