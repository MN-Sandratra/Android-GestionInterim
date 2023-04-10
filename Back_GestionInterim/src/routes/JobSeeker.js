const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');

// Route pour créer un nouveau chercheur d'emploi
router.post('/', async (req, res) => {
    try {
        const jobSeeker = new JobSeeker(req.body);
        await jobSeeker.save();
        res.status(201).json(jobSeeker);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

// Route pour récupérer tous les chercheurs d'emploi
router.get('/', async (req, res) => {
    try {
        const jobSeekers = await JobSeeker.find();
        res.json(jobSeekers);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// Route pour récupérer un chercheur d'emploi spécifique
router.get('/:id', getJobSeeker, (req, res) => {
    res.json(res.jobSeeker);
});

// Route pour mettre à jour un chercheur d'emploi spécifique
router.put('/:id', getJobSeeker, async (req, res) => {
    if (req.body.firstName != null) {
        res.jobSeeker.firstName = req.body.firstName;
    }
    if (req.body.lastName != null) {
        res.jobSeeker.lastName = req.body.lastName;
    }
    if (req.body.nationality != null) {
        res.jobSeeker.nationality = req.body.nationality;
    }
    if (req.body.dateOfBirth != null) {
        res.jobSeeker.dateOfBirth = req.body.dateOfBirth;
    }
    if (req.body.phoneNumber != null) {
        res.jobSeeker.phoneNumber = req.body.phoneNumber;
    }
    if (req.body.email != null) {
        res.jobSeeker.email = req.body.email;
    }
    if (req.body.city != null) {
        res.jobSeeker.city = req.body.city;
    }
    if (req.body.cv != null) {
        res.jobSeeker.cv = req.body.cv;
    }
    if (req.body.comments != null) {
        res.jobSeeker.comments = req.body.comments;
    }
    try {
        const updatedJobSeeker = await res.jobSeeker.save();
        res.json(updatedJobSeeker);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

// Route pour supprimer un chercheur d'emploi spécifique
router.delete('/:id', getJobSeeker, async (req, res) => {
    try {
        await res.jobSeeker.remove();
        res.json({ message: 'Chercheur d\'emploi supprimé' });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

// Middleware pour récupérer un chercheur d'emploi par son identifiant
async function getJobSeeker(req, res, next) {
    try {
        const jobSeeker = await JobSeeker.findById(req.params.id);
        if (!jobSeeker) {
            return res.status(404).json({ message: 'Chercheur d\'emploi non trouvé' });
        }
        res.jobSeeker = jobSeeker;
        next();
    } catch (err) {
        return res.status(500).json({ message: err.message });
    }
}

module.exports = router;
