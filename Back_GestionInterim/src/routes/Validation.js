const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const Employer = require('../models/employer');
const Agence = require('../models/agence');
const crypto = require('crypto');

router.post('/validate', async (req, res) => {
  const email = req.body.email;
  const validationCode = req.body.validationCode;
  console.log("my email: " + email);

  try {
    const jobSeeker = await JobSeeker.findOne({ email: email });
    const employer = await Employer.findOne({ email1: email });
    const agence = await Agence.findOne({ email1: email });

    if (!jobSeeker && !employer && !agence) {
      return res.status(404).json({ message: 'Utilisateur non trouvé' });
    }

    if (jobSeeker && jobSeeker.validationCode !== validationCode) {
      return res.status(400).json({ message: "Code de validation incorrect" });
    }

    if (employer && employer.validationCode !== validationCode) {
      return res.status(400).json({ message: "Code de validation incorrect" });
    }

    if (agence && agence.validationCode !== validationCode) {
      return res.status(400).json({ message: "Code de validation incorrect" });
    }

    let userType = "";
    if (jobSeeker) {
      jobSeeker.isValidated = true;
      await jobSeeker.save();
      userType = "jobseekers";
      user = jobSeeker;
    } else if (employer) {
      employer.isValidated = true;
      await employer.save();
      userType = "employers";
      user = employer;
    }
    else if (agence) {
      agence.isValidated = true;
      await agence.save();
      userType = "agence";
      user = agence;
    }
    

    return res.status(200).json({ message: "Validation réussie", type: userType, user:user});
  } catch (err) {
    return res.status(500).json({ message: err.message });
  }
});



//Envoyer le code de validation
router.post('/code', async (req, res) => {
  const email = req.body.email;
  const confirmationCode =  Math.floor(Math.random() * 9000) + 1000;

  try {
      const jobSeeker = await JobSeeker.findOne({ email: email });
      const employer = await Employer.findOne({ email1: email });
      const agence = await Agence.findOne({ email1: email });

      if (!jobSeeker && !employer && !agence) {
          return res.status(404).json({ message: "Utilisateur non trouvé" });
      }

      if(jobSeeker){
        jobSeeker.validationCode = confirmationCode;
        await jobSeeker.save();  
      }
      
      if(employer){
        employer.validationCode = confirmationCode;
        await employer.save();  
      }

      if(agence){
        agence.validationCode = confirmationCode;
        await agence.save();  
      }

      mailService.sendConfirmationCodeByEmail(email, confirmationCode)
      .then(() => {
          console.log(`Code de confirmation envoyé à ${email}`);
      })
      .catch((error) => {
          console.error(`Erreur lors de l'envoi du code de confirmation : ${error}`);
      });
      res.status(201).json({ message: "Code de confirmation renvoyé" });
  } catch (err) {
      return res.status(500).json({ message: err.message });
  }
});


module.exports = router;
  