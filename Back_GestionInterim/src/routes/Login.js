const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const Employer = require('../models/employer');
const crypto = require('crypto');

router.post('/', async (req, res) => {
    const { email, password } = req.body;
  
    try {
      const jobSeeker = await JobSeeker.findOne({ email });
      const employer = await Employer.findOne({ email });
      if (!jobSeeker && !employer) {
        return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
      }
  
      if (jobSeeker && jobSeeker.password !== password) {
        return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
      }
  
      if (employer && employer.password !== password) {
        return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
      }
  
      if (jobSeeker && !jobSeeker.isValidated) {
        return res.status(400).json({ message: 'Compte non validé' });
      }
  
      if (employer && !employer.isValidated) {
        return res.status(400).json({ message: 'Compte non validé' });
      }
  
      let userType = "";
      if(jobSeeker) {
        userType = "jobSeeker";
      } else if (employer) {
        userType = "employer";
      }
  
      return res.status(200).json({ message: 'Connexion réussie', type: userType });
    } catch (err) {
      console.error(err);
      res.status(500).json({ message: 'Erreur du serveur' });
    }
  });
  
  module.exports = router;
  