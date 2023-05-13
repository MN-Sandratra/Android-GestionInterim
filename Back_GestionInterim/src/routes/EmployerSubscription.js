const express = require('express');
const router = express.Router();
const EmployerSubscription = require('../models/employerSubscription');
const Employer = require('../models/employer');
const Subscription = require('../models/subscription');
const {Paiement} = require('../models/paiement');


// GET all employer subscriptions
router.get('/', async (req, res) => {
  try {
    const employerSubscriptions = await EmployerSubscription.find();
    res.json(employerSubscriptions);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET a single employer subscription
router.get('/:id', getEmployerSubscription, (req, res) => {
  res.json(res.employerSubscription);
});

// CREATE a new employer subscription
router.post('/', async (req, res) => {
  try {
      const { type, email } = req.body;

      // Find Employer and Subscription
      const employer = await Employer.findOne({ email1: email });
      const subscription = await Subscription.findOne({ type: type });

      if(!employer || !subscription) {
          return res.status(400).json({ validation: false, message: 'Employer or Subscription not found.' });
      }

      // Create a new Paiement
      const paiementToken = Math.random().toString(36).substr(2, 10);
      const paiement = new Paiement({ type, email, paiementToken });

      await paiement.save();

      // Calculate the expiration date based on the duration of the subscription
      let expirationDate = new Date();
      
      if (subscription.duration.unit === 'jours') {
        expirationDate.setDate(expirationDate.getDate() + subscription.duration.value);
      } 
      
      else if (subscription.duration.unit === 'semaines') {
        expirationDate.setDate(expirationDate.getDate() + subscription.duration.value * 7);
      } 
      
      else if (subscription.duration.unit === 'mois') {
        expirationDate.setMonth(expirationDate.getMonth() + subscription.duration.value);
      }


      // Create a new EmployerSubscription
      const employerSubscription = new EmployerSubscription({
          employer: employer._id,
          subscription: subscription._id,
          paiement: paiement._id,
          // Set the expirationDate according to your business logic
          expirationDate: expirationDate,
      });

      await employerSubscription.save();

      // Send response with validation status and created paiement and employerSubscription objects
      res.json({ validation: true, paiement, employerSubscription });
  } catch (err) {
      console.error(err.message);
      res.status(500).json({ validation: false, message: 'Server Error' });
  }
});


// UPDATE an existing employer subscription
router.put('/:id', getEmployerSubscription, async (req, res) => {
  if (req.body.employer != null) {
    res.employerSubscription.employer = req.body.employer;
  }
  if (req.body.subscription != null) {
    res.employerSubscription.subscription = req.body.subscription;
    res.employerSubscription.expirationDate = calculateExpirationDate(req.body.subscription.duration);
  }
  if (req.body.paiement != null) {
    res.employerSubscription.paiement = req.body.paiement;
  }

  try {
    const updatedEmployerSubscription = await res.employerSubscription.save();
    res.json(updatedEmployerSubscription);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE an employer subscription
router.delete('/:id', getEmployerSubscription, async (req, res) => {
  try {
    await res.employerSubscription.remove();
    res.json({ message: 'Employer subscription deleted' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// middleware function to get a single employer subscription by ID
async function getEmployerSubscription(req, res, next) {
  try {
    const employerSubscription = await EmployerSubscription.findById(req.params.id);
    if (employerSubscription == null) {
      return res.status(404).json({ message: 'Cannot find employer subscription' });
    }
    res.employerSubscription = employerSubscription;
    next();
  } catch (err) {
    return res.status(500).json({ message: err.message });
  }
}

// function to calculate the expiration date based on the duration of the subscription
function calculateExpirationDate(duration) {
  const now = new Date();
  let expirationDate = now;

  switch (duration.unit) {
    case 'jours':
      expirationDate.setDate(now.getDate() + duration.value);
      break;
    case 'semaines':
      expirationDate.setDate(now.getDate() + (duration.value * 7));
      break;
    case 'mois':
      expirationDate.setMonth(now.getMonth() + duration.value);
      break;
  }

  return expirationDate;
}

module.exports = router;
