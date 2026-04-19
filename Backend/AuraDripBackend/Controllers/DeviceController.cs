using AuraDripBackend.Data;
using AuraDripBackend.Models;

using Microsoft.AspNetCore.Mvc;

using PostHog;

namespace AuraDripBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")] // /api/device
    public class DeviceController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly IPostHogClient _posthog;

        public DeviceController(AppDbContext context, IPostHogClient posthog)
        {
            _context = context;
            _posthog = posthog;
        }

        [HttpPost("sync")] // /api/device/sync
        public async Task<IActionResult> Sync([FromBody] Telemetry data)
        {
            // 1. Зберігаємо телеметрію
            data.Timestamp = DateTime.UtcNow;
            _context.Add(data);

            // 2. Шукаємо конкретну рослину в базі даних, щоб дізнатися її поточні налаштування
            var plant = await _context.Plants.FindAsync(data.PlantId);
            if (plant == null) return NotFound(new { message = "Plant not found" });

            // 3. Запам'ятовуємо, чи була команда на полив
            bool shouldWaterNow = plant.HasPendingWaterCommand;

            // Якщо команда була, нам треба очистити "поштову скриньку".
            if (shouldWaterNow)
            {
                plant.HasPendingWaterCommand = false;
            }

            // 4. Зберігаємо в базу і нову телеметрію, і очищену скриньку!
            await _context.SaveChangesAsync();

            // Відправляємо дані від пристрою в аналітику
            _posthog?.Capture("esp32_device", "telemetry_received", new Dictionary<string, object>
            {
                { "plant_id", plant.Id },
                { "soil_moisture", data.SoilMoisture },
                { "did_force_water", shouldWaterNow } // Записуємо, чи полила помпа квітку прямо зараз
            });

            return Ok(new
            {
                // ESP32 візьме це число і оновить свою умову поливу
                minMoistureThreshold = plant.MinMoistureThreshold,
                controlMode = plant.ControlMode,
                // Єдиний випадок, коли сервер втручається - це якщо користувач 
                // прямо зараз натиснув у додатку велику кнопку "полити примусово".
                forceWaterNow = shouldWaterNow
            });
        }
    }
}