const express = require('express');
const router = express.Router();
const Employer = require('../models/employer');
const multer=require('multer');
const mailService = require('../services/Mail');

const getEmployer = async (req, res) => {
    try {
        const employer = await Employer.findById(req.params.id);
        if (!employer) {
            return res.status(404).json({ message: "Employer not found" });
        }
        return res.json(employer);
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ message: "Server Error" });
    }
};

// Route pour récupérer tous les employeurs
router.get('/', async (req, res) => {
    try {
        const employers = await Employer.find();
        res.json(employers);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Route pour récupérer un employeur spécifique
router.get('/:id', getEmployer, (req, res) => {
    res.json(res.employer);
});

// Route pour créer un nouvel employeur
router.post('/', async (req, res) => {
    const confirmationCode = Math.floor(Math.random() * 9000) + 1000;
    try{

        const employer = new Employer({
        companyName: req.body.companyName,
        department: req.body.department,
        subDepartment: req.body.subDepartment,
        nationalNumber: req.body.nationalNumber,
        contactPerson1: req.body.contactPerson1,
        contactPerson2: req.body.contactPerson2,
        email1: req.body.email1,
        password:req.body.password,
        email2: req.body.email2,
        phone1: req.body.phone1,
        phone2: req.body.phone2,
        address: req.body.address,
        publicLinks: req.body.publicLinks,
        validationCode:confirmationCode,
    });

    const newEmployer = await employer.save();

    const toEmail = newEmployer.email1;

    mailService.sendConfirmationCodeByEmail(toEmail, confirmationCode)
    .then(() => {
        console.log(`Code de confirmation envoyé à ${toEmail}`);
    })
    .catch((error) => {
        console.error(`Erreur lors de l'envoi du code de confirmation : ${error}`);
    });

    res.status(201).json(newEmployer);
    }
    catch (err){
        res.status(400).json({ message: err.message });
    }
});

// Route pour mettre à jour un employeur
router.put('/:id', getEmployer, async (req, res) => {
    if (req.body.companyName != null) {
        res.employer.companyName = req.body.companyName;
    }

    if (req.body.department != null) {
        res.employer.department = req.body.department;
    }

    if (req.body.subDepartment != null) {
        res.employer.subDepartment = req.body.subDepartment;
    }

    if (req.body.nationalNumber != null) {
        res.employer.nationalNumber = req.body.nationalNumber;
    }

    if (req.body.contactPerson1 != null) {
        res.employer.contactPerson1 = req.body.contactPerson1;
    }

    if (req.body.contactPerson2 != null) {
        res.employer.contactPerson2 = req.body.contactPerson2;
    }

    if (req.body.email1 != null) {
        res.employer.email1 = req.body.email1;
    }

    if (req.body.email2 != null) {
        res.employer.email2 = req.body.email2;
    }

    if (req.body.phone1 != null) {
        res.employer.phone1 = req.body.phone1;
    }

    if (req.body.phone2 != null) {
        res.employer.phone2 = req.body.phone2;
    }

    if (req.body.address != null) {
        res.employer.address = req.body.address;
    }

    if (req.body.publicLinks != null) {
        res.employer.publicLinks = req.body.publicLinks;
    }

    try {
        const updatedEmployer = await res.employer.save();
        res.json(updatedEmployer);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Route pour supprimer un employeur
router.delete('/:id', getEmployer, async (req, res) => {
    try {
        await res.employer.remove();
        res.json({ message: 'Employer deleted successfully' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});



module.exports = router;