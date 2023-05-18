const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const subscriptionSchema = new Schema({
    type: {
        type: String,
        required: true
    },
    price: {
        type: Number,
        required: true
    },
    shortDescription :{
        type: String,
        required: true
    },
    description:{
        type: String,
        required: true
    },
    conditions: {
        type: String,
        required: true
    },
    cancellationConditions: {
        type: String,
        required: true
    },
    duration: {
        value: {
            type: Number,
            required: true
        },
        unit: {
            type: String,
            enum: ['jours', 'semaines', 'mois'], 
            required: true
        }
    },
    subscriptionName: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model('Subscription', subscriptionSchema);
