const express = require('express');
const mongoose = require('mongoose');
const Offer = require('../models/offer');
const { CandidatureOffre, etatsCandidaturesEnum } = require('../models/candidatureOffre');
const Employer = require('../models/employer'); 
const JobSeeker = require('../models/jobSeeker'); 
const Candidature = require('../models/candidature'); 
const multer = require('multer');

// Configuration de multer pour la gestion des fichiers
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/'); // Chemin de destination pour enregistrer les fichiers
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = `${Date.now()}-${Math.round(Math.random() * 1E9)}`;
    cb(null, `${file.fieldname}-${uniqueSuffix}`); // Nom du fichier enregistré
  }
});

const upload = multer({ storage });




const router = express.Router();

// Créer une association entre une candidature et une offre
router.post('/', async (req, res) => {
  try {
    const { candidatureId, offreId } = req.body;

    const association = new CandidatureOffre({
      candidature: candidatureId,
      offre: offreId
    });

    await association.save();

    res.json({ message: 'Association candidature-offre créée avec succès' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la création de l\'association candidature-offre' });
  }
});


router.post('/employersCandidature', async(req,res) => {
  try {
    const { candidatureId, offreId, status } = req.body;

    // Vérifie si le status est dans l'enum
    if (!etatsCandidaturesEnum.includes(status)) {
      return res.status(400).json({ message: "Status non valide" });
    }

    // Trouve l'instance de CandidatureOffre et la met à jour
    const updatedCandidatureOffre = await mongoose.model('CandidatureOffre').findOneAndUpdate(
      { candidature: candidatureId, offre: offreId }, // critères de recherche
      { status: status }, // mise à jour du champ status
    );

    if (!updatedCandidatureOffre) {
      return res.status(404).json({ message: 'Association candidature-offre non trouvée' });
    }

    res.json({ message: 'Candidature traitée !'});
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la création de l\'association candidature-offre',error: error.message});
  }
});



// Créer une association entre une candidature et une offre
router.post('/jobseekerOffre', upload.fields([{ name: 'contenuCv' }, { name: 'contenuLm' }]), async (req, res) => {
  try {
    const { firstName, lastName, nationality, dateOfBirth, email, telephone, cv, lm, identifiantOffre} = req.body;

    let jobSeeker = null;

    console.log(req.body)

    if(email){
      jobSeeker = await JobSeeker.findOne({ email: email });
    }

    if(!jobSeeker && telephone){
      jobSeeker = await JobSeeker.findOne({ telephone: telephone });
    }

    const candidature = new Candidature({
      firstName,
      lastName,
      nationality,
      dateOfBirth,
      jobSeeker: jobSeeker.id
    });
    
    if (cv && cv.length > 0) {
      candidature.cv = cv;
    }

    if (lm && lm.length > 0) {
      candidature.lm = lm;
    }
    
    await candidature.save();

    const candidatureId = candidature._id;

    console.log(candidatureId)

    const association = new CandidatureOffre({
      candidature: candidatureId,
      offre: identifiantOffre
    });

    await association.save();

    res.json({ message: 'Nouvelle candidature créée avec succès' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la création de la candidature' });
  }
});


// Obtenir toutes les associations candidature-offre
router.get('/', async (req, res) => {
  try {
    const associations = await CandidatureOffre.find().populate('candidature offre');
    res.json(associations);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la récupération des associations candidature-offre' });
  }
});
// Candidature - Offre pour un employeur spécifique
router.get('/employers', async (req, res) => {
  try {
    const { email, status} = req.query; 

    const employer = await Employer.findOne({ email1: email });

    if (!employer) {
        return res.status(404).json({ message: 'Employeur non trouvé.' });
    }

    const offers = await Offer.find({ employeur: employer._id });
    const offerIds = offers.map(offer => offer._id);

    const candidatureOffres = await CandidatureOffre.find({ 
      offre: { $in: offerIds },
      status: status || 'en attente'
    }).populate('candidature').populate('offre');

    const enrichedCandidatureOffres = await Promise.all(candidatureOffres.map(async (candidatureOffre) => {
      const jobSeeker = await JobSeeker.findById(candidatureOffre.candidature.jobSeeker);
      return {
        ...candidatureOffre._doc,
        candidature: {
          ...candidatureOffre.candidature._doc,
          email: jobSeeker.email,
          phoneNumber: jobSeeker.phoneNumber
        }
      };
    }));

    res.json(enrichedCandidatureOffres);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la récupération des associations candidature-offre' });
  }
});

// Candidature - Offre pour un interimaire spécifique
router.get('/jobseekers', async (req, res) => {
  try {
    const { email, telephone, status } = req.query; 

    let jobSeeker = null;

      if(email){
        jobSeeker = await JobSeeker.findOne({ email: email });
      }

      if(!jobSeeker && telephone){
        jobSeeker = await JobSeeker.findOne({ phoneNumber: telephone });
      }

    if (!jobSeeker) {
        return res.status(404).json({ message: 'Interimaire non trouvé.' });
    }

    console.log(jobSeeker._id)

    const candidatures = await Candidature.find({ jobSeeker: jobSeeker._id });

    const candidatureIds = candidatures.map(candidature => candidature._id);

    const candidatureOffres = await CandidatureOffre.find({ 
      candidature: { $in: candidatureIds },
      status: status || 'en attente'
    })
    .populate('candidature')
    .populate('offre');

    res.json(candidatureOffres);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la récupération des associations candidature-offre' });
  }
});


// Supprimer une association candidature-offre
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    await CandidatureOffre.findByIdAndDelete(id);

    res.json({ message: 'Association candidature-offre supprimée avec succès' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la suppression de l\'association candidature-offre' });
  }
});
// Obtenir une association candidature-offre par ID
router.get('/:id', async (req, res) => {
    try {
      const { id } = req.params;
      const association = await CandidatureOffre.findById(id).populate('candidature offre');
      
      if (!association) {
        return res.status(404).json({ message: 'Association candidature-offre non trouvée' });
      }
      
      res.json(association);
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Une erreur est survenue lors de la récupération de l\'association candidature-offre' });
    }
  });
  
  // Mettre à jour une association candidature-offre
  router.put('/:id', async (req, res) => {
    try {
      const { id } = req.params;
      const { candidatureId, offreId } = req.body;
  
      const association = await CandidatureOffre.findByIdAndUpdate(id, { candidature: candidatureId, offre: offreId }, { new: true });
  
      if (!association) {
        return res.status(404).json({ message: 'Association candidature-offre non trouvée' });
      }
  
      res.json({ message: 'Association candidature-offre mise à jour avec succès', association });
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Une erreur est survenue lors de la mise à jour de l\'association candidature-offre' });
    }
  });

module.exports = router;
