const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const multer=require('multer');

const storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, './uploads/');
    },
    filename: function(req, file, cb) {
        cb(null, Date.now() + file.originalname);
    }
});

const fileFilter = (req, file, cb) => {
    if (file.mimetype === 'application/pdf') {
        cb(null, true);
    } else {
        cb(null, false);
    }
};

const upload = multer({
    storage: storage,
    limits: {
        fileSize: 1024 * 1024 * 5
    },
    fileFilter: fileFilter
});

// Route pour créer un nouveau chercheur d'emploi
router.post('/', upload.single('cv'), async (req, res) => {
    try {
        const jobSeeker = new JobSeeker({
            firstName: req.body.firstName,
            lastName: req.body.lastName,
            nationality: req.body.nationality,
            dateOfBirth: req.body.dateOfBirth,
            phoneNumber: req.body.phoneNumber,
            email: req.body.email,
            city: req.body.city,
            comments: req.body.comments
        });
        if (req.file) {
            jobSeeker.cv = req.file.path;
        }

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
router.put('/:id', getJobSeeker,upload.single('cv'), async (req, res) => {
    console.log(req.body);
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
    if (req.file) {
        res.jobSeeker.cv = req.file.path;
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
router.delete('/:id', async (req, res) => {
    try {
      const result = await JobSeeker.findByIdAndDelete(req.params.id);
      if (!result) {
        return res.status(404).json({ message: 'Chercheur d\'emploi non trouvé' });
      }
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
