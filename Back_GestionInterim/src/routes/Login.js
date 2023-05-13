const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const Employer = require('../models/employer');
const EmployerSubscription = require('../models/employerSubscription');

const crypto = require('crypto');

router.post('/', async (req, res) => {
  const { email, password } = req.body;

  try {
    const jobSeeker = await JobSeeker.findOne({ email : email });
    const employer1 = await Employer.findOne({ email1 : email }); 
    const employer2 = await Employer.findOne({ email2 : email }); 
    
    if (!jobSeeker && !employer1 && !employer2) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (jobSeeker && jobSeeker.password !== password) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (employer1 && employer1.password !== password || employer2 && employer2.password !== password) {
      return res.status(400).json({ message: 'Email incorrect ou mot de passe incorrect' });
    }

    if (jobSeeker && !jobSeeker.isValidated) {
      return res.status(400).json({ message: 'Compte non validé' });
    }

    if (employer1 && !employer1.isValidated || employer2 && !employer2.isValidated) {
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

    let userType = "";
    let nom = "";
    
    if(jobSeeker) {
      userType = "jobSeeker";
      nom = jobSeeker.lastName
    } else if (employer1 || employer2) {
      userType = "employer";
      if(employer1){
        nom = employer1.companyName
      }
      else{
        nom = employer2.companyName
      }
    }

    return res.status(200).json({ 
      message: 'Connexion réussie', 
      type: userType, 
      nom: nom, 
      hasSubscription: hasSubscription // add subscription status to the response
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Erreur du serveur' });
  }
});
  
  module.exports = router;
  