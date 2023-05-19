const fetch = require('node-fetch');

async function getCoordinatesFromCity(city) {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?city=${encodeURIComponent(city)}&format=json`
    );
    const data = await response.json();

    if (data.length > 0) {
      const { lat, lon } = data[0];
      return { latitude: parseFloat(lat), longitude: parseFloat(lon) };
    } else {
      throw new Error(`No coordinates found for city ${city}`);
    }
  } catch (error) {
    console.error(error);
    return null;
  }
}

async function getCoordinatesFromAddress(address) {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`
    );
    const data = await response.json();

    if (data.length > 0) {
      console.log(data[0])
  
      const { lat, lon} = data[0];
    
      return {latitude: parseFloat(lat), longitude: parseFloat(lon) };
    } else {
      throw new Error(`No coordinates found for address ${address}`);
    }
  } catch (error) {
    console.error(error);
    return null;
  }
}



function getDistanceInKm(lat1, lon1, lat2, lon2) {
    const R = 6371; // rayon de la Terre en km
    const dLat = toRadians(lat2 - lat1);
    const dLon = toRadians(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(toRadians(lat1)) *
        Math.cos(toRadians(lat2)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const d = R * c;
    return d;
}

function toRadians(degrees) {
    return (degrees * Math.PI) / 180;
}

module.exports = {
  getCoordinatesFromCity,
  getDistanceInKm,
  getCoordinatesFromAddress
};
