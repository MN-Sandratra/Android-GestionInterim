const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const Employer = require('../models/employer');
const Agence = require('../models/agence');
const EmployerSubscription = require('../models/employerSubscription');
const AgenceSubscription = require('../models/agenceSubscription');

const crypto = require('crypto');

router.post('/', async (req, res) => {
  const { email, password } = req.body;

  try {
    const jobSeeker = await JobSeeker.findOne({ email : email });
    const employer1 = await Employer.findOne({ email1 : email }); 
    const agence = await Agence.findOne({ email1 : email }); 
    const employer2 = await Employer.findOne({ email2 : email }); 
    
    if (!jobSeeker && !employer1 && !employer2 && !agence) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (jobSeeker && jobSeeker.password !== password) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (employer1 && employer1.password !== password || employer2 && employer2.password !== password) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (agence && agence.password !== password) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (jobSeeker && !jobSeeker.isValidated) {
      return res.status(400).json({ message: 'Compte non validé' });
    }

    if (employer1 && !employer1.isValidated || employer2 && !employer2.isValidated) {
      return res.status(400).json({ message: 'Compte non validé' });
    }
    if (agence && !agence.isValidated) {
      return res.status(400).json({ message: 'Compte non validé' });
    }

    // Check if employer has an active subscription
    let hasSubscription = false;
    if (employer1 || employer2) {
      const employerId = employer1 ? employer1._id : employer2._id;
      const employerSubscription = await EmployerSubscription.findOne({
        employer: employerId,
        expirationDate: { $gt: new Date() }
      });
      hasSubscription = !!employerSubscription;
    }

    if (agence) {
      const agenceId = agence._id;
      const agenceSubscription = await AgenceSubscription.findOne({
        agence: agenceId,
        expirationDate: { $gt: new Date() }
      });
      hasSubscription = !!agenceSubscription;
    }

    let user = null;
    let userType = "";

    if(jobSeeker) {
      userType = "jobSeeker";
      user = jobSeeker;
    } else if (employer1 || employer2) {
      userType = "employer";
      if(employer1){
        user = employer1
      }
      else{
        user = employer2
      }
    } else if(agence){
      userType = "agence";
      user = agence
    }

    return res.status(200).json({ 
      message: 'Connexion réussie', 
      type: userType, 
      hasSubscription: hasSubscription,
      user: user
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});
  
  module.exports = router;
  