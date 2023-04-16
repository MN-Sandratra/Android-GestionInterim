const express = require('express');
const router = express.Router();
const { Payment } = require('../models/payement');

// GET all payments
router.get('/', async (req, res) => {
  try {
    const payments = await Payment.find();
    res.send(payments);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

// GET payment by ID
router.get('/:id', async (req, res) => {
  try {
    const payment = await Payment.findById(req.params.id);
    if (!payment) return res.status(404).send('Payment not found');
    res.send(payment);
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('Payment not found');
    res.status(500).send('Server Error');
  }
});

// CREATE a new payment
router.post('/', async (req, res) => {
  try {
    const { type } = req.body;
    // generate payment token
    const paymentToken = Math.random().toString(36).substr(2, 10);
    const payment = new Payment({ type, paymentToken });
    await payment.save();
    res.send(payment);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

// UPDATE a payment by ID
router.patch('/:id', async (req, res) => {
  try {
    const payment = await Payment.findById(req.params.id);
    if (!payment) return res.status(404).send('Payment not found');
    const { type } = req.body;
    payment.type = type;
    await payment.save();
    res.send(payment);
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('Payment not found');
    res.status(500).send('Server Error');
  }
});

// DELETE a payment by ID
router.delete('/:id', async (req, res) => {
  try {
    const payment = await Payment.findById(req.params.id);
    if (!payment) return res.status(404).send('Payment not found');
    await payment.remove();
    res.send('Payment deleted successfully');
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('Payment not found');
    res.status(500).send('Server Error');
  }
});

module.exports = router;
