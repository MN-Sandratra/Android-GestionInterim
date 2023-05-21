const express = require('express');
const { CandidatureOffre } = require('../models/candidatureOffre');

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
