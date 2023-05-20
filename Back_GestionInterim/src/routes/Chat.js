const express = require('express');
const router = express.Router();
const Chat = require('../models/chat');
const JobSeeker = require('../models/jobSeeker');

// Route pour envoyer un message
router.post('/messages', async (req, res) => {
  try {
    const { senderId, receiverId, content } = req.body;

    // Vérifier si les chercheurs d'emploi existent
    const sender = await JobSeeker.findById(senderId);
    const receiver = await JobSeeker.findById(receiverId);
    if (!sender || !receiver) {
      return res.status(404).json({ message: 'Chercheur d\'emploi introuvable' });
    }

    // Créer le message
    const message = new Chat({
      sender: senderId,
      receiver: receiverId,
      content: content,
    });
    await message.save();

    return res.status(200).json({ message: 'Message envoyé avec succès' });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: 'Erreur serveur' });
  }
});

// Route pour récupérer les messages entre deux chercheurs d'emploi
router.get('/messages/:senderId/:receiverId', async (req, res) => {
  try {
    const { senderId, receiverId } = req.params;

    // Vérifier si les chercheurs d'emploi existent
    const sender = await JobSeeker.findById(senderId);
    const receiver = await JobSeeker.findById(receiverId);
    if (!sender || !receiver) {
      return res.status(404).json({ message: 'Chercheur d\'emploi introuvable' });
    }

    // Récupérer les messages entre les chercheurs d'emploi
    const messages = await Chat.find({
      $or: [
        { sender: senderId, receiver: receiverId },
        { sender: receiverId, receiver: senderId },
      ],
    }).sort({ timestamp: 1 });

    return res.status(200).json(messages);
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: 'Erreur serveur' });
  }
});

module.exports = router;
