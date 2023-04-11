require('dotenv').config();
const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
    host: "smtp.elasticemail.com",
    port: 2525,
    secure: false,
  auth: {
    user: process.env.EMAIL_ADDRESS,
    pass: process.env.EMAIL_PASSWORD
  }
});


function sendConfirmationCodeByEmail(toEmail, confirmationCode) {
  const mailOptions = {
    from: process.env.EMAIL_ADDRESS,
    to: toEmail,
    subject: "Code de confirmation ✔", // Subject line
    text: `Merci de renseignez ce code confirmation a l'application : ${confirmationCode}`, // plain text body
    html: `<b>Merci de renseignez ce code confirmation a l'application</b>  : ${confirmationCode}`, // html body
  };

  return transporter.sendMail(mailOptions);
}

module.exports = {
  sendConfirmationCodeByEmail
};
