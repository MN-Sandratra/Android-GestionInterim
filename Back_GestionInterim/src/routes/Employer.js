const express = require('express');
const router = express.Router();
const Employer = require('../models/employer');
const multer=require('multer');
const mailService = require('../services/Mail');

const getEmployer = async (req, res) => {
    try {
        const employer = await Employer.findById(req.params.id);
        if (!employer) {
            return res.status(404).json({ message: "Employer not found" });
        }
        return res.json(employer);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ message: "Server Error" });
    }
};

// Route pour récupérer tous les employeurs
router.get('/', async (req, res) => {
    try {
        const employers = await Employer.find();
        res.json(employers);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Route pour récupérer un employeur spécifique
router.get('/:id', getEmployer, (req, res) => {
    res.json(res.employer);
});

// Route pour créer un nouvel employeur
router.post('/', async (req, res) => {
    const confirmationCode = Math.floor(Math.random() * 9000) + 1000;
    try{

        const employer = new Employer({
        companyName: req.body.companyName,
        department: req.body.department,
        subDepartment: req.body.subDepartment,
        nationalNumber: req.body.nationalNumber,
        contactPerson1: req.body.contactPerson1,
        contactPerson2: req.body.contactPerson2,
        email1: req.body.email1,
        password:req.body.password,
        email2: req.body.email2,
        phone1: req.body.phone1,
        phone2: req.body.phone2,
        address: req.body.address,
        ville: req.body.ville,
        publicLinks: req.body.publicLinks,
        validationCode:confirmationCode,
    });

    const newEmployer = await employer.save();

    const toEmail = newEmployer.email1;

    mailService.sendConfirmationCodeByEmail(toEmail, confirmationCode)
    .then(() => {
        console.log(`Code de confirmation envoyé à ${toEmail}`);
    })
    .catch((error) => {
        console.error(`Erreur lors de l'envoi du code de confirmation : ${error}`);
    });

    res.status(201).json(newEmployer);
    }
    catch (err){
        res.status(400).json({ message: err.message });
    }
});

// Route pour mettre à jour un employeur
router.put('/:email1', async (req, res) => {
    try {
      const employer = await Employer.findOne({ email1: req.params.email1 });
  
      if (!employer) {
        return res.status(404).json({ message: 'Employer not found' });
      }
  
      if (req.body.companyName != null) {
        employer.companyName = req.body.companyName;
      }
  
      if (req.body.department != null) {
        employer.department = req.body.department;
      }
  
      if (req.body.subDepartment != null) {
        employer.subDepartment = req.body.subDepartment;
      }
  
      if (req.body.nationalNumber != null) {
        employer.nationalNumber = req.body.nationalNumber;
      }
  
      if (req.body.contactPerson1 != null) {
        employer.contactPerson1 = req.body.contactPerson1;
      }
  
      if (req.body.contactPerson2 != null) {
        employer.contactPerson2 = req.body.contactPerson2;
      }
  
      if (req.body.email2 != null) {
        employer.email2 = req.body.email2;
      }
  
      if (req.body.phone1 != null) {
        employer.phone1 = req.body.phone1;
      }
  
      if (req.body.phone2 != null) {
        employer.phone2 = req.body.phone2;
      }
  
      if (req.body.address != null) {
        employer.address = req.body.address;
      }

      if (req.body.ville != null) {
        employer.ville = req.body.ville;
      }
  
      if (req.body.website != null) {
        employer.website = req.body.website;
      }
  
      if (req.body.linkedin != null) {
        employer.linkedin = req.body.linkedin;
      }

      if (req.body.facebook != null) {
        employer.facebook = req.body.facebook;
      }
  
      const updatedEmployer = await employer.save();
      res.json(updatedEmployer);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
});


  
router.delete('/:email1', async (req, res) => {
  try {
      // Trouver l'employeur à supprimer
      const employerToDelete = await Employer.findOneAndDelete({ email1: req.params.email1 });

      if (employerToDelete) {
          // Importer les modèles nécessaires
          const Offer = require('../models/offer');
          const EmployerSubscription = require('../models/employerSubscription');
          const CandidatureOffre = require('../models/candidatureOffre').CandidatureOffre;

          // Trouver toutes les offres de l'employeur
          const offers = await Offer.find({ employeur: employerToDelete._id });

          // Supprimer toutes les offres associées à cet employeur
          await Offer.deleteMany({ employeur: employerToDelete._id });

          // Supprimer tous les abonnements de l'employeur
          await EmployerSubscription.deleteMany({ employer: employerToDelete._id });

          // Supprimer toutes les CandidaturesOffres associées aux offres de l'employeur
          const offerIds = offers.map(offer => offer._id);
          await CandidatureOffre.deleteMany({ offre: { $in: offerIds } });

          res.json({ message: 'Employer, associated offers, subscriptions, and candidatures deleted successfully' });
      } else {
          res.status(404).json({ message: 'Employer not found' });
      }
  } catch (error) {
      res.status(500).json({ message: error.message });
  }
});






module.exports = router;