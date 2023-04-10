const express = require('express');
const router = express.Router();
const Subscription = require('../models/subscription');

// GET all subscriptions
router.get('/', async (req, res) => {
  try {
    const subscriptions = await Subscription.find();
    res.json(subscriptions);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET one subscription
router.get('/:id', getSubscription, (req, res) => {
  res.json(res.subscription);
});

// POST create subscription
router.post('/', async (req, res) => {
  const subscription = new Subscription({
    type: req.body.type,
    price: req.body.price,
    conditions: req.body.conditions,
    cancellationConditions: req.body.cancellationConditions,
  });

  try {
    const newSubscription = await subscription.save();
    res.status(201).json(newSubscription);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

router.put('/:id', getSubscription, async (req, res) => {
  if (req.body.type != null) {
    res.subscription.type = req.body.type;
  }
  if (req.body.price != null) {
    res.subscription.price = req.body.price;
  }
  if (req.body.conditions != null) {
    res.subscription.conditions = req.body.conditions;
  }
  if (req.body.cancellationConditions != null) {
    res.subscription.cancellationConditions = req.body.cancellationConditions;
  }

  try {
    const updatedSubscription = await res.subscription.save();
    res.json(updatedSubscription);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE subscription
router.delete('/:id', getSubscription, async (req, res) => {
  try {
    await res.subscription.remove();
    res.json({ message: 'Subscription deleted' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Middleware function to get subscription by id
async function getSubscription(req, res, next) {
  let subscription;
  try {
    subscription = await Subscription.findById(req.params.id);
    if (subscription == null) {
      return res.status(404).json({ message: 'Subscription not found' });
    }
  } catch (err) {
    return res.status(500).json({ message: err.message });
  }

  res.subscription = subscription;
  next();
}

module.exports = router;
