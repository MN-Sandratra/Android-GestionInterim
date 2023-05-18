const express = require('express');
const router = express.Router();

// Créer un groupe
router.post('/', async (req, res) => {
  try {
    const { name, creatorId } = req.body;

    const creator = await JobSeeker.findById(creatorId);
    if (!creator) {
      return res.status(404).json({ error: 'Créateur non trouvé' });
    }

    const newGroup = await Group.create({
      name,
      creator: creator._id,
      members: [{ user: creator._id, isAdmin: true }]
    });

    res.status(201).json(newGroup);
  } catch (error) {
    console.error('Erreur lors de la création du groupe', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la création du groupe' });
  }
});

// Obtenir tous les groupes
router.get('/', async (req, res) => {
  try {
    const groups = await Group.find().populate('creator', 'firstName lastName');
    res.status(200).json(groups);
  } catch (error) {
    console.error('Erreur lors de la récupération des groupes', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la récupération des groupes' });
  }
});

// Obtenir un groupe par ID
router.get('/:groupId', async (req, res) => {
  try {
    const group = await Group.findById(req.params.groupId).populate('creator', 'firstName lastName');
    if (!group) {
      return res.status(404).json({ error: 'Groupe non trouvé' });
    }

    res.status(200).json(group);
  } catch (error) {
    console.error('Erreur lors de la récupération du groupe', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la récupération du groupe' });
  }
});

// Ajouter un membre à un groupe
router.post('/:groupId/members', async (req, res) => {
  try {
    const group = await Group.findById(req.params.groupId);
    const jobSeeker = await JobSeeker.findById(req.body.jobSeekerId);

    if (!group || !jobSeeker) {
      return res.status(404).json({ error: 'Groupe ou utilisateur non trouvé' });
    }

    group.members.push({ user: jobSeeker._id });
    await group.save();

    res.status(200).json(group);
  } catch (error) {
    console.error('Erreur lors de l\'ajout de l\'utilisateur au groupe', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de l\'ajout de l\'utilisateur au groupe' });
  }
});

// Supprimer un groupe
router.delete('/:groupId', async (req, res) => {
    try {
      const group = await Group.findByIdAndDelete(req.params.groupId);
      if (!group) {
        return res.status(404).json({ error: 'Groupe non trouvé' });
      }
  
      res.status(200).json({ message: 'Groupe supprimé avec succès' });
    } catch (error) {
      console.error('Erreur lors de la suppression du groupe', error);
      res.status(500).json({ error: 'Une erreur est survenue lors de la suppression du groupe' });
    }
  });
  
  // Modifier un groupe
router.put('/:groupId', async (req, res) => {
    try {
      const group = await Group.findByIdAndUpdate(req.params.groupId, req.body, { new: true });
      if (!group) {
        return res.status(404).json({ error: 'Groupe non trouvé' });
      }
  
      res.status(200).json(group);
    } catch (error) {
      console.error('Erreur lors de la modification du groupe', error);
      res.status(500).json({ error: 'Une erreur est survenue lors de la modification du groupe' });
    }
  });

module.exports = router;
