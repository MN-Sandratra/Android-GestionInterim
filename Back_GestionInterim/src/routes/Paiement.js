const express = require('express');
const router = express.Router();
const { Paiement } = require('../models/paiement');

const Employer = require('../models/employer');
const Subscription = require('../models/subscription');
const EmployerSubscription = require('../models/employerSubscription');


// GET all paiements
router.get('/', async (req, res) => {
  try {
    const paiements = await Paiement.find();
    res.send(paiements);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

// GET paiement by ID
router.get('/:id', async (req, res) => {
  try {
    const paiement = await Paiement.findById(req.params.id);
    if (!paiement) return res.status(404).send('Paiement not found');
    res.send(paiement);
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('Paiement not found');
    res.status(500).send('Server Error');
  }
});

// CREATE a new paiement
router.post('/', async (req, res) => {
  try {
    const { type, email, date } = req.body;

    // generate paiement token
    const paiementToken = Math.random().toString(36).substr(2, 10);
    const paiement = new Paiement({ type, email, date, paiementToken });

    await paiement.save();

    // Send response with validation status and created paiement object
    res.json({ validation: true, paiement });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ validation: false, message: 'Server Error' });
  }
});

// UPDATE a paiement by ID
router.patch('/:id', async (req, res) => {
  try {
    const paiement = await Paiement.findById(req.params.id);
    if (!paiement) return res.status(404).send('paiement not found');
    const { type } = req.body;
    paiement.type = type;
    await paiement.save();
    res.send(paiement);
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('paiement not found');
    res.status(500).send('Server Error');
  }
});

// DELETE a paiement by ID
router.delete('/:id', async (req, res) => {
  try {
    const paiement = await Paiement.findById(req.params.id);
    if (!paiement) return res.status(404).send('paiement not found');
    await paiement.remove();
    res.send('paiement deleted successfully');
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') return res.status(404).send('paiement not found');
    res.status(500).send('Server Error');
  }
});

module.exports = router;
