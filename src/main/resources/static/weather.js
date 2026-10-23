async function getWeather() {

    const inputElement = document.getElementById('locationInput') || document.querySelector('input');

    if (!inputElement) {
        console.error("Could not find the input element. Make sure your input has id='locationInput'");
        return;
    }

    const location = inputElement.value;
    const resultDiv = document.getElementById('weatherResult');
    const errorDiv = document.getElementById('error');

    errorDiv.style.display = 'none';
    resultDiv.style.display = 'none';

    if (!location) return;
        try {
            const response = await fetch(`/api/weather?location=${encodeURIComponent(location)}`);

            if (response.status === 429) {
                throw new Error('Too many requests. Please wait a minute and try again.');
            }
            if (response.status === 400) {
                throw new Error('Could not find that location.');
            }
            if (response.status === 502) {
                throw new Error('The weather service is unavailable right now. Please try again later.');
            }
            if (!response.ok) {
                throw new Error('Could not get the weather. Please try again later.');
            }

            const data = await response.json();

            document.getElementById('cityName').innerText = data.name + ', ' + data.sys.country;
            document.getElementById('temperature').innerText = Math.round(data.main.temp) + '°C';
            document.getElementById('description').innerText = data.weather[0].description;

            resultDiv.style.display = 'block';
        } catch (error) {
        console.error("Error:", error);
        errorDiv.innerText = error.message;
        errorDiv.style.display = 'block';
    }
}

window.onload = getWeather;
