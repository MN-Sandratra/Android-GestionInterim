const express = require('express');
const router = express.Router();
const Chat = require('../models/chat')
const JobSeeker = require('../models/jobSeeker')
const Employer = require('../models/employer');
//const Agency = require('../models/agency');

// Route pour envoyer un message
router.post('/', async (req, res) => {
  try {
    const { sender, receiver, content } = req.body;

    // Vérifier si les chercheurs d'emploi, employeur ou agence existent
    const sendera = await JobSeeker.findById(sender) || await Employer.findById(sender);
    const receivera = await JobSeeker.findById(receiver) || await Employer.findById(receiver);

    if (!sendera || !receivera) {
      return res.status(404).json({ message: 'Utilisateur introuvable' });
    }

    // Créer le message
    const message = new Chat({
      sender: sender,
      receiver: receiver,
      content: content,
    });
    await message.save();

    return res.status(200).json({ message: 'Message envoyé avec succès' });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: 'Erreur serveur' });
  }
});

// Route pour récupérer les messages entre deux utilisateurs (chercheur d'emploi, employeur ou agence)
router.get('/:senderId/:receiverId', async (req, res) => {
  try {
    const { senderId, receiverId } = req.params;

    // Vérifier si les chercheurs d'emploi, employeur ou agence existent
    const sender = await JobSeeker.findById(senderId) || await Employer.findById(senderId);
    const receiver = await JobSeeker.findById(receiverId) || await Employer.findById(receiverId);
    if (!sender || !receiver) {
      return res.status(404).json({ message: 'Utilisateur introuvable' });
    }

    // Récupérer les messages entre les utilisateurs
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

router.get('/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    console.log(userId);
    // Vérifier si l'utilisateur existe
    const user = await JobSeeker.findById(userId) || await Employer.findById(userId);
    if (!user) {
      return res.status(404).json({ message: 'Utilisateur introuvable' });
    }

    // Récupérer les conversations distinctes de l'utilisateur
    const conversations = await Chat.aggregate([
      { $match: { $or: [{ sender: userId }, { receiver: userId }] } },
      {
        $group: {
          _id: { $cond: [{ $eq: ['$sender', userId] }, '$receiver', '$sender'] },
          lastMessage: { $last: '$content' },
          timestamp: { $last: '$timestamp' }
        }
      },
      { $sort: { timestamp: -1 } }
    ]);
    const participantsIds = conversations.map(conversation => conversation._id);
    const participants = await Promise.all(participantsIds.map(async participantId => {
      const jobSeeker = await JobSeeker.findById(participantId);
      if (jobSeeker) {
        return { id: participantId, name: `${jobSeeker.firstName} ${jobSeeker.lastName}` };
      } else {
        const employer = await Employer.findById(participantId);
        return { id: participantId, name: employer.companyName };
      }
    }));

    // Créer la réponse contenant les conversations, les personnes et le dernier message
    const response = conversations.map((conversation, index) => ({
      participant: participants[index],
      lastMessage: conversation.lastMessage,
      timestamp: conversation.timestamp
    }));

    return res.status(200).json(response);
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: 'Erreur serveur' });
  }
});


module.exports = router;

