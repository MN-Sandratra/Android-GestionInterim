const express = require('express');
const multer = require('multer');
const { Candidature } = require('../models/candidature');

const router = express.Router();

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

// Obtenir toutes les candidatures
router.get('/', async (req, res) => {
  try {
    const candidatures = await Candidature.find();
    res.json(candidatures);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la récupération des candidatures' });
  }
});

// Obtenir une candidature spécifique
router.get('/:id', async (req, res) => {
  try {
    const candidature = await Candidature.findById(req.params.id);
    if (!candidature) {
      return res.status(404).json({ message: 'Candidature non trouvée' });
    }
    res.json(candidature);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la récupération de la candidature' });
  }
});

// Mettre à jour une candidature
router.put('/:id', upload.fields([{ name: 'cv' }, { name: 'lm' }]), async (req, res) => {
  try {
    const { firstName, lastName, nationality, dateOfBirth,jobSeeker } = req.body;
    const { cv, lm } = req.files;

    const candidature = await Candidature.findById(req.params.id);
    if (!candidature) {
      return res.status(404).json({ message: 'Candidature non trouvée' });
    }
    candidature.jobSeeker=jobSeeker;
    candidature.firstName = firstName;
    candidature.lastName = lastName;
    candidature.nationality = nationality;
    candidature.dateOfBirth = dateOfBirth;

    if (cv && cv.length > 0) {
      candidature.cv = cv[0].path;
    }

    if (lm && lm.length > 0) {
      candidature.lm = lm[0].path;
    }

    await candidature.save();

    res.json({ message: 'Candidature mise à jour avec succès' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la mise à jour de la candidature' });
  }
});

router.post('/', upload.fields([{ name: 'cv' }, { name: 'lm' }]), async (req, res) => {
    try {
      const { firstName, lastName, nationality, dateOfBirth, jobSeekerId } = req.body;
      const { cv, lm } = req.files;
  
      const candidature = new Candidature({
        firstName,
        lastName,
        nationality,
        dateOfBirth,
        jobSeeker: jobSeekerId
      });
  
      if (cv && cv.length > 0) {
        candidature.cv = cv[0].path;
      }
  
      if (lm && lm.length > 0) {
        candidature.lm = lm[0].path;
      }
  
      await candidature.save();
  
      res.json({ message: 'Nouvelle candidature créée avec succès' });
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Une erreur est survenue lors de la création de la candidature' });
    }
  });

// Supprimer une candidature
router.delete('/:id', async (req, res) => {
  try {
    const candidature = await Candidature.findByIdAndDelete(req.params.id);
    if (!candidature) {
      return res.status(404).json({ message: 'Candidature non trouvée' });
    }
    res.json({ message: 'Candidature supprimée avec succès' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Une erreur est survenue lors de la suppression de la candidature' });
  }
});

module.exports = router;
