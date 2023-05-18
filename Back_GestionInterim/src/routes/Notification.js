const express = require('express');
const router = express.Router();
const Notification = require('../models/notification');

// Récupérer toutes les notifications d'un utilisateur
router.get('/:userId', async (req, res) => {
  try {
    const userId = req.params.userId;

    const notifications = await Notification.find({ recipient: userId }).sort({ createdAt: -1 });
    res.status(200).json(notifications);
  } catch (error) {
    console.error('Erreur lors de la récupération des notifications', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la récupération des notifications' });
  }
});

// Marquer une notification comme lue
router.put('/:notificationId/markAsRead', async (req, res) => {
  try {
    const notificationId = req.params.notificationId;

    const notification = await Notification.findByIdAndUpdate(notificationId, { $set: { isRead: true } }, { new: true });
    if (!notification) {
      return res.status(404).json({ error: 'Notification non trouvée' });
    }

    res.status(200).json(notification);
  } catch (error) {
    console.error('Erreur lors de la mise à jour de la notification', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la mise à jour de la notification' });
  }
});

// Supprimer une notification
router.delete('/:notificationId', async (req, res) => {
  try {
    const notificationId = req.params.notificationId;

    const notification = await Notification.findByIdAndDelete(notificationId);
    if (!notification) {
      return res.status(404).json({ error: 'Notification non trouvée' });
    }

    res.status(200).json({ message: 'Notification supprimée avec succès' });
  } catch (error) {
    console.error('Erreur lors de la suppression de la notification', error);
    res.status(500).json({ error: 'Une erreur est survenue lors de la suppression de la notification' });
  }
});

module.exports = router;
