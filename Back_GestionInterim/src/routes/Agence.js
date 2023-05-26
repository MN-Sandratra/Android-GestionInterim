const express = require('express');
const router = express.Router();
const Agence = require('../models/agence');
const multer=require('multer');
const mailService = require('../services/Mail');

const getAgence = async (req, res) => {
    try {
        const agence = await Agence.findById(req.params.id);
        if (!agence) {
            return res.status(404).json({ message: "Agence not found" });
        }
        return res.json(agence);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ message: "Server Error" });
    }
};

// Route pour récupérer toutes les agences
router.get('/', async (req, res) => {
    try {
        const agences = await Agence.find();
        res.json(agences);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Route pour récupérer une agence spécifique
router.get('/:id', getAgence, (req, res) => {
    res.json(res.agence);
});

// Route pour créer une nouvelle agence
router.post('/', async (req, res) => {
    const confirmationCode = Math.floor(Math.random() * 9000) + 1000;
    try{

      const existingAgence = await Agence.findOne({ email1: req.body.email1 });
      if (existingAgence) {
          return res.status(400).json({ message: "Une agence avec cet email existe déjà" });
      }

        const agence = new Agence({
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

    const newAgence = await agence.save();

    const toEmail = newAgence.email1;

    mailService.sendConfirmationCodeByEmail(toEmail, confirmationCode)
    .then(() => {
        console.log(`Code de confirmation envoyé à ${toEmail}`);
    })
    .catch((error) => {
        console.error(`Erreur lors de l'envoi du code de confirmation : ${error}`);
    });

    res.status(201).json(newAgence);
    }
    catch (err){
        res.status(400).json({ message: err.message });
    }
});

// Route pour mettre à jour une agence
router.put('/:email1', async (req, res) => {
    try {
      const agence = await Agence.findOne({ email1: req.params.email1 });
  
      if (!agence) {
        return res.status(404).json({ message: 'Agence not found' });
      }
  
      if (req.body.companyName != null) {
        agence.companyName = req.body.companyName;
      }
  
      if (req.body.department != null) {
        agence.department = req.body.department;
      }
  
      if (req.body.subDepartment != null) {
        agence.subDepartment = req.body.subDepartment;
      }
  
      if (req.body.nationalNumber != null) {
        agence.nationalNumber = req.body.nationalNumber;
      }
  
      if (req.body.contactPerson1 != null) {
        agence.contactPerson1 = req.body.contactPerson1;
      }
  
      if (req.body.contactPerson2 != null) {
        agence.contactPerson2 = req.body.contactPerson2;
      }
  
      if (req.body.email2 != null) {
        agence.email2 = req.body.email2;
      }
  
      if (req.body.phone1 != null) {
        agence.phone1 = req.body.phone1;
      }
  
      if (req.body.phone2 != null) {
        agence.phone2 = req.body.phone2;
      }
  
      if (req.body.address != null) {
        agence.address = req.body.address;
      }

      if (req.body.ville != null) {
        agence.ville = req.body.ville;
      }
  
      if (req.body.website != null) {
        agence.website = req.body.website;
      }
  
      if (req.body.linkedin != null) {
        agence.linkedin = req.body.linkedin;
      }

      if (req.body.facebook != null) {
        agence.facebook = req.body.facebook;
      }
  
      const updatedAgence = await agence.save();
      res.json(updatedAgence);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
});


  
router.delete('/:email1', async (req, res) => {
  try {
      // Trouver l'agence à supprimer
      const agenceToDelete = await Agence.findOneAndDelete({ email1: req.params.email1 });

      if (agenceToDelete) {
          // Importer les modèles nécessaires
          const Offer = require('../models/offerAgence');
          const AgenceSubscription = require('../models/agenceSubscription');
          const CandidatureOffre = require('../models/candidatureOffre').CandidatureOffre;

          // Trouver toutes les offres de l'agence
          const offers = await Offer.find({ agence: agenceToDelete._id });

          // Supprimer toutes les offres associées à cette agence
          await Offer.deleteMany({ agence: agenceToDelete._id });

          // Supprimer tous les abonnements de l'agence
          await AgenceSubscription.deleteMany({ agence: agenceToDelete._id });

          // Supprimer toutes les CandidaturesOffres associées aux offres de l'agence
          const offerIds = offers.map(offer => offer._id);
          await CandidatureOffre.deleteMany({ offre: { $in: offerIds } });

          res.json({ message: 'Agence, associated offers, subscriptions, and candidatures deleted successfully' });
      } else {
          res.status(404).json({ message: 'Agence not found' });
      }
  } catch (error) {
      res.status(500).json({ message: error.message });
  }
});






module.exports = router;