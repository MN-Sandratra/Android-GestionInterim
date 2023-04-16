const express = require('express');
const router = express.Router();
const EmployerSubscription  = require('../models/employerSubscription');

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
  const employerSubscription = new EmployerSubscription({
    employer: req.body.employer,
    subscription: req.body.subscription,
    payment: req.body.payment
  });

  try {
    const newEmployerSubscription = await employerSubscription.save();
    res.status(201).json(newEmployerSubscription);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// UPDATE an existing employer subscription
router.put('/:id', getEmployerSubscription, async (req, res) => {
  if (req.body.employer != null) {
    res.employerSubscription.employer = req.body.employer;
  }
  if (req.body.subscription != null) {
    res.employerSubscription.subscription = req.body.subscription;
  }
  if (req.body.payment != null) {
    res.employerSubscription.payment = req.body.payment;
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

module.exports = router;
