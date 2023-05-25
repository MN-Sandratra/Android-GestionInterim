const express = require('express');
const router = express.Router();
const JobSeeker = require('../models/jobSeeker');
const mongoose = require('mongoose');

const multer=require('multer');
const mailService = require('../services/Mail');

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
router.post('/', upload.single('contenuCv'), async (req, res) => {
    const confirmationCode = Math.floor(Math.random() * 9000) + 1000;
    try {
        const jobSeeker = new JobSeeker({
            firstName: req.body.firstName,
            lastName: req.body.lastName,
            nationality: req.body.nationality,
            dateOfBirth: req.body.dateOfBirth,
            phoneNumber: req.body.phoneNumber,
            email: req.body.email,
            city: req.body.city,
            comments: req.body.comments,
            validationCode:confirmationCode,
            password: req.body.password
        });
        if (req.file) {
            jobSeeker.cv = req.file.path;
        }

        await jobSeeker.save();
        
        const toEmail = jobSeeker.email;

        mailService.sendConfirmationCodeByEmail(toEmail, confirmationCode)
        .then(() => {
            console.log(`Code de confirmation envoyé à ${toEmail}`);
        })
        .catch((error) => {
            console.error(`Erreur lors de l'envoi du code de confirmation : ${error}`);
        });

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
router.put('/', upload.single('contenuCv'), async (req, res) => {    

    let jobSeeker;
    // Determine if contact is an email or a phone number
    if (req.body.phoneNumber) {
        jobSeeker = await JobSeeker.findOne({ phoneNumber: req.body.phoneNumber });
    } 
    
    if(!jobSeeker){
        jobSeeker = await JobSeeker.findOne({ email: req.body.email });
    }

    if (!jobSeeker) {
        return res.status(404).json({ message: 'Chercheur d\'emploi non trouvé' });
    }

    if (req.body.firstName != null) {
        jobSeeker.firstName = req.body.firstName;
    }
    if (req.body.lastName != null) {
        jobSeeker.lastName = req.body.lastName;
    }
    if (req.body.nationality != null) {
        jobSeeker.nationality = req.body.nationality;
    }
    if (req.body.dateOfBirth != null) {
        jobSeeker.dateOfBirth = req.body.dateOfBirth;
    }
    if (req.body.phoneNumber != null) {
        jobSeeker.phoneNumber = req.body.phoneNumber;
    }
    if (req.body.email != null) {
        jobSeeker.email = req.body.email;
    }
    if (req.body.city != null) {
        jobSeeker.city = req.body.city;
    }
    if (req.file) {
        jobSeeker.cv = req.file.path;
    }
    if (req.body.comments != null) {
        jobSeeker.comments = req.body.comments;
    }
    if (req.body.password != null) {
        jobSeeker.password = req.body.password;
    }
    try {
        const updatedJobSeeker = await jobSeeker.save();
        res.json(updatedJobSeeker);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

router.delete('/:contact', async (req, res) => {
    try {
        let jobSeeker;

        // Determine if contact is an email or a phone number
        if (req.params.contact.includes('@')) {
            // It's an email
            jobSeeker = await JobSeeker.findOne({ email: req.params.contact });
        } else {
            // It's a phone number
            jobSeeker = await JobSeeker.findOne({ phoneNumber: req.params.contact });
        }

        if (!jobSeeker) {
            return res.status(404).json({ message: 'Job seeker not found' });
        }

        const jobSeekerId = jobSeeker._id;

        // Deleting associated Candidature and CandidatureOffre
        const candidatures = await mongoose.model('Candidature').find({ jobSeeker: jobSeekerId });
        await mongoose.model('Candidature').deleteMany({ jobSeeker: jobSeekerId });

        const candidatureIds = candidatures.map(candidature => candidature._id);
        await mongoose.model('CandidatureOffre').deleteMany({ candidature: { $in: candidatureIds } });

        // Deleting the JobSeeker
        await JobSeeker.findOneAndDelete({ _id: jobSeekerId });

        res.json({ message: 'Job seeker and associated candidatures deleted' });
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
