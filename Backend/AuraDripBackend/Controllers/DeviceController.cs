using AuraDripBackend.Data;
using AuraDripBackend.Models;

using Microsoft.AspNetCore.Mvc;

namespace AuraDripBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")] // /api/device
    public class DeviceController : ControllerBase
    {
        private readonly AppDbContext _context;

        public DeviceController(AppDbContext context)
        {
            _context = context;
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