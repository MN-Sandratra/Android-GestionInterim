const express = require('express');
const bodyParser = require('body-parser')
const { default: mongoose } = require('mongoose');
require('dotenv').config();

const dblink=process.env.CONNECTION_DB;
console.log(dblink);
const app = express();
const port = 8000;
const serverAddress = process.env.ADDRESS;

const JobSeekerRouter =require("./routes/JobSeeker");
const EmployerRouter =require("./routes/Employer");
const SubscriptionRouter =require("./routes/Subscription");
const LoginRouter =require("./routes/Login");
const ValidationRouter =require("./routes/Validation");
const PaiementRouter =require("./routes/Paiement");
const EmployerSubscriptionRouter =require("./routes/EmployerSubscription");
const OfferRouter =require("./routes/Offers");
const GroupRouter =require("./routes/Group");
const NotificationRouter =require("./routes/Notification");
const CandidatureRouter =require("./routes/Candidature");
const MessageRouter =require("./routes/Chat");
const candidatureOffreRouter =require("./routes/CandidatureOffre");


app.use(bodyParser.urlencoded({ extended: false }))
// parse application/json
app.use(bodyParser.json())
app.use(function (req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', '*');
  next();
});

app.use('/api/jobseekers',JobSeekerRouter);
app.use('/api/employers',EmployerRouter);
app.use('/api/subscriptions',SubscriptionRouter);
app.use('/api/login',LoginRouter);
app.use('/api/validation', ValidationRouter);
app.use('/api/paiement',PaiementRouter);
app.use('/api/empsubscription', EmployerSubscriptionRouter);
app.use('/api/offers', OfferRouter);
app.use('/api/groups', GroupRouter);
app.use('/api/notifications',NotificationRouter);
app.use('/api/candidatures',CandidatureRouter);
app.use('/api/messages',MessageRouter);
app.use('/api/candidatureOffres',candidatureOffreRouter);


app.get('/', (req, res) => {
    res.send('Hello, World!');
  });
  
  
  async function startServer(){
    await mongoose.connect(
      ""+dblink,
    ).then(()=>console.log("Connected to MongoDb Database"))
    .catch(()=>console.log("Connection Failed"));
    app.listen(port, serverAddress,() => {
      console.log(`Server running at http://${serverAddress}:${port}`);
    });
  }
  
  startServer();
  