const express = require('express');
const router = express.Router();
const AgenceSubscription = require('../models/agenceSubscription');
const Agence = require('../models/agence');
const Subscription = require('../models/subscription');
const {Paiement} = require('../models/paiement');


// GET all agence subscriptions
router.get('/', async (req, res) => {
  try {
    const agenceSubscriptions = await AgenceSubscription.find();
    res.json(agenceSubscriptions);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET a single agence subscription
router.get('/:id', getAgenceSubscription, (req, res) => {
  res.json(res.agenceSubscription);
});

// CREATE a new agence subscription
router.post('/', async (req, res) => {
  try {
      const { type, email } = req.body;

      console.log(type, email)

      // Find Agence and Subscription
      const agence = await Agence.findOne({ email1: email });
      const subscription = await Subscription.findOne({ type: type });

      if(!agence || !subscription) {
          return res.status(400).json({ validation: false, message: 'Agence or Subscription not found.' });
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


      // Create a new AgenceSubscription
      const agenceSubscription = new AgenceSubscription({
          agence: agence._id,
          subscription: subscription._id,
          paiement: paiement._id,
          // Set the expirationDate according to your business logic
          expirationDate: expirationDate,
      });

      await agenceSubscription.save();

      // Send response with validation status and created paiement and agenceSubscription objects
      res.json({ validation: true, paiement, agenceSubscription });
  } catch (err) {
      console.error(err.message);
      res.status(500).json({ validation: false, message: 'Server Error' });
  }
});


// UPDATE an existing agence subscription
router.put('/:id', getAgenceSubscription, async (req, res) => {
  if (req.body.agence != null) {
    res.agenceSubscription.agence = req.body.agence;
  }
  if (req.body.subscription != null) {
    res.agenceSubscription.subscription = req.body.subscription;
    res.agenceSubscription.expirationDate = calculateExpirationDate(req.body.subscription.duration);
  }
  if (req.body.paiement != null) {
    res.agenceSubscription.paiement = req.body.paiement;
  }

  try {
    const updatedAgenceSubscription = await res.agenceSubscription.save();
    res.json(updatedAgenceSubscription);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE an agence subscription
router.delete('/:id', getAgenceSubscription, async (req, res) => {
  try {
    await res.agenceSubscription.remove();
    res.json({ message: 'Agence subscription deleted' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// middleware function to get a single agence subscription by ID
async function getAgenceSubscription(req, res, next) {
  try {
    const agenceSubscription = await AgenceSubscription.findById(req.params.id);
    if (agenceSubscription == null) {
      return res.status(404).json({ message: 'Cannot find agence subscription' });
    }
    res.agenceSubscription = agenceSubscription;
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
