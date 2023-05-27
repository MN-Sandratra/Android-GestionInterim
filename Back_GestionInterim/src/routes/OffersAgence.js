const express = require('express');
const router = express.Router();
const Offer = require('../models/offer');
const Agence = require('../models/agence');
const cityService = require('../services/Cities');
const offerAgence = require('../models/offerAgence');

router.get('/', async (req, res) => {
  try {
    const { metier, ville, latitude, longitude, rayon, dateDebut, dateFin } = req.query;

    console.log(ville)

    if ((!latitude || !longitude) && !ville) {
      return res.status(400).json({ message: 'Missing latitude and/or longitude' });
    }

    // Récupération du nom de l'agence à partir de l'identifiant 
    const offers = await offerAgence.find().populate('agence', 'companyName -_id');
    const filteredOffers = [];

    console.log(offers)

    // Pour chaque offre
    for (const offer of offers) {
      // Comparaison de la distance entre l'utilisateur et les offres
      
      if (!offer.disponibilite) {
        continue;
      }

      let distance;
      let cityLat, cityLng;
      if (ville) {
        const { latitude: cityLat, longitude: cityLng } = await cityService.getCoordinatesFromCity(ville);
        distance = cityService.getDistanceInKm(cityLat, cityLng, offer.latitude, offer.longitude);
      } else {
        cityLat = offer.latitude;
        cityLng = offer.longitude;
        distance = cityService.getDistanceInKm(latitude, longitude, cityLat, cityLng);
      }

      // Si distance plus de "rayon", je propose l'offre à l'utilisateur

      if (distance > rayon) {
        continue;
      }
 
      if (metier && offer.intitule !== metier) {
        continue;
      }

      if (dateDebut) {
        const [jour, mois, annee] = dateDebut.split("/");
        if (new Date(annee, mois - 1, jour) > offer.dateDebut) {
          continue;
        }
      }
      
      if (dateFin) {
        const [jourFin, moisFin, anneeFin] = dateFin.split("/");
        if (new Date(anneeFin, moisFin - 1, jourFin) < offer.dateFin) {
          continue;
        }
      }
      

      filteredOffers.push(offer);
    }

    const modifiedOffers = filteredOffers.map(offer => {
      const modifiedOffer = offer.toObject();
      modifiedOffer.employer = offer.agence.companyName;
      return modifiedOffer;
    });

    console.log(modifiedOffers)

    res.json(modifiedOffers);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// GET /offers/:email
router.get('/agences', async (req, res) => {
  try {
    const { email, metier, dateDebut, dateFin} = req.query;
    const agence = await Agence.findOne({ email1: email });
    if (!agence) {
      return res.status(404).json({ message: 'Agence not found' });
    }

    const offers = await offerAgence.find({ agence: agence._id }).populate('agence', 'companyName -_id');
    const filteredOffers = [];

    // Pour chaque offre
    for (const offer of offers) {
      // Comparaison de la distance entre l'utilisateur et les offres
  
      if (metier && offer.intitule !== metier) {
        continue;
      }

      if (dateDebut) {
        const [jour, mois, annee] = dateDebut.split("/");
        if (new Date(annee, mois - 1, jour) > offer.dateDebut) {
          continue;
        }
      }
      
      if (dateFin) {
        const [jourFin, moisFin, anneeFin] = dateFin.split("/");
        if (new Date(anneeFin, moisFin - 1, jourFin) < offer.dateFin) {
          continue;
        }
      }
    
      filteredOffers.push(offer);
    }

    const modifiedOffers = filteredOffers.map(offer => {
      const modifiedOffer = offer.toObject();
      modifiedOffer.agence = offer.agence.companyName;
      return modifiedOffer;
    });

    res.json(modifiedOffers);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});



// GET /offers/:id
router.get('/:id', async (req, res) => {
  try {
    const offer = await Offer.findById(req.params.id);
    if (offer) {
      res.json(offer);
    } else {
      res.status(404).json({ message: 'Offer not found' });
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// POST /offers
router.post('/', async (req, res) => {
  
  console.log(req.body);  

  const offers = req.body;

  try {
    const newOffers = await Promise.all(offers.map(async offer => {
      const agence = await Agence.findOne({ email1: offer.employeur });
      if (!agence) { // Vérifie si l'employeur n'a pas été trouvé
        throw new Error('Agence not found');
      }

      const locationData = await cityService.getCoordinatesFromCity(offer.ville);
      if (!locationData) {
        throw new Error('Unable to find city and coordinates from address');
      }
  
      const newOffer = new offerAgence({
        agence: agence.id,
        intitule: offer.intitule,
        dateDebut: offer.dateDebut,
        dateFin: offer.dateFin,
        experienceRequise: offer.experienceRequise,
        description: offer.description,
        tauxHoraire: offer.tauxHoraire,
        heureDebut: offer.heureDebut,
        disponibilite: offer.disponibilite,
        etat: offer.etat,
        ville: offer.ville, // Ajout de la ville
        adressePostale: offer.adressePostale, // Ajout de l'adresse postale
        latitude: locationData.latitude, // Ajout de la latitude
        longitude: locationData.longitude // Ajout de la longitude
      });
  
      return newOffer.save();
    }));

    res.status(201).json(newOffers);
  } catch (error) {
    console.log(error.message)
    res.status(400).json({ message: error.message });
  }
});



// PUT /offers/:id
router.put('/:id', async (req, res) => {
    try {
      const offer = await offerAgence.findById(req.params.id);
      if (offer) {
        offer.agence = req.body.agence || offer.agence;
        offer.intitule = req.body.intitule || offer.intitule;
        offer.dateDebut = req.body.dateDebut || offer.dateDebut;
        offer.dateFin = req.body.dateFin || offer.dateFin;
        offer.experienceRequise = req.body.experienceRequise || offer.experienceRequise;
        offer.description = req.body.description || offer.description;
        offer.tauxHoraire = req.body.tauxHoraire || offer.tauxHoraire;
        offer.disponibilite = req.body.disponibilite || offer.disponibilite;
        offer.etat = req.body.etat || offer.etat;
        offer.remuneration = req.body.remuneration || offer.remuneration;
        offer.ville = req.body.ville || offer.ville; // Ajout de la ville
        offer.adressePostale = req.body.adressePostale || offer.adressePostale; // Ajout de l'adresse postale
  
        const updatedOffer = await offer.save();
        res.json(updatedOffer);
      } else {
        res.status(404).json({ message: 'Offer not found' });
      }
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  });
  
  // DELETE /offers/:id
  router.delete('/:id', async (req, res) => {
    try {
      const offer = await Offer.findByIdAndDelete(req.params.id);
      if (offer) {
        res.json({ message: 'Offer deleted' });
      } else {
        res.status(404).json({ message: 'Offer not found' });
      }
    } catch (error) {
      res.status(500).json({ message: error.message});
    }
  });
  

  function formatDate(date) {
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    return `${day}/${month}/${year}`;
  }


module.exports = router;