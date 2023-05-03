const mongoose = require("mongoose");
const Employer = require("./models/employer");
const Offer = require("./models/offer");
const JobSeeker = require("./models/jobSeeker");

const port = 8000;
const serverAddress = process.env.ADDRESS;
const dblink = process.env.DB_LINK; 

async function seedDatabase() {
  try {
    // Création de 3 instances d'employeur
    const employer1 = new Employer({
      companyName: "Company 1",
      nationalNumber: "123456789",
      email1: "company1@example.com",
      password: "password1",
      address: "123 Street, City 1",
    });

    const employer2 = new Employer({
      companyName: "Company 2",
      nationalNumber: "987654321",
      email1: "company2@example.com",
      password: "password2",
      address: "456 Street, City 2",
    });

    const employer3 = new Employer({
      companyName: "Company 3",
      nationalNumber: "456789123",
      email1: "company3@example.com",
      password: "password3",
      address: "789 Street, City 3",
    });

    await Employer.insertMany([employer1, employer2, employer3]);

    // Création de 3 instances d'offres
    const offer1 = new Offer({
      employeur: employer1._id,
      intitule: "Job Title 1",
      dateDebut: new Date(),
      dateFin: new Date(),
      experienceRequise: 1,
      description: "Description 1",
      tauxHoraire: 15,
      remuneration: 3000,
      ville: "City 1",
      adressePostale: "123 Street, City 1",
    });

    const offer2 = new Offer({
      employeur: employer2._id,
      intitule: "Job Title 2",
      dateDebut: new Date(),
      dateFin: new Date(),
      experienceRequise: 2,
      description: "Description 2",
      tauxHoraire: 20,
      remuneration: 4000,
      ville: "City 2",
      adressePostale: "456 Street, City 2",
    });

    const offer3 = new Offer({
      employeur: employer3._id,
      intitule: "Job Title 3",
      dateDebut: new Date(),
      dateFin: new Date(),
      experienceRequise: 3,
      description: "Description 3",
      tauxHoraire: 25,
      remuneration: 5000,
      ville: "City 3",
      adressePostale: "789 Street, City 3",
    });

    await Offer.insertMany([offer1, offer2, offer3]);

    // Création de 3 instances de chercheurs d'emploi
    const jobSeeker1 = new JobSeeker({
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@example.com",
      password: "password1",
    });

    const jobSeeker2 = new JobSeeker({
      firstName: "Jane",
      lastName: "Doe",
      email: "jane.doe@example.com",
      password: "password2",
    });

    const jobSeeker3 = new JobSeeker({
        firstName: "Jim",
        lastName: "Doe",
        email: "jim.doe@example.com",
        password: "password3",
      });
      
      await JobSeeker.insertMany([jobSeeker1, jobSeeker2, jobSeeker3]);
      
      console.log("Database seeded successfully!");
    } catch (error) {
        console.error("Error while seeding database:", error);
        } finally {
        mongoose.connection.close();
        }
        }
        
async function startServer(){
    await mongoose.connect(
        ""+dblink,
    ).then(()=>console.log("Connected to MongoDb Database"))
    .catch(()=>console.log("Connection Failed"));
    app.listen(port, serverAddress,() => {
    console.log(`Server running at http://${serverAddress}:${port}`);
});
}

startServer();
seedDatabase();
