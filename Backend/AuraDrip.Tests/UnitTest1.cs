using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Xunit;
using AuraDripBackend.Controllers;
using AuraDripBackend.Data;
using AuraDripBackend.Models;

namespace AuraDrip.Tests
{
    public class ControllersTests
    {
        private AppDbContext GetInMemoryDbContext()
        {
            var options = new DbContextOptionsBuilder<AppDbContext>()
                .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString()) // Унікальне ім'я для кожного тесту
                .Options;

            return new AppDbContext(options);
        }

        // ================= ТЕСТИ ДЛЯ DeviceController =================

        [Fact]
        public async Task Sync_WhenPlantNotFound_ReturnsNotFound()
        {
            // Arrange
            using var context = GetInMemoryDbContext();
            var controller = new DeviceController(context, null);
            var telemetry = new Telemetry { PlantId = 99 }; // Рослини з ID 99 не існує

            // Act
            var result = await controller.Sync(telemetry);

            // Assert
            Assert.IsType<NotFoundObjectResult>(result); // Перевіряємо, що повернувся статус 404
        }
        //Користувач у мобільному додатку натиснув кнопку "Полити", сервер це запам'ятав,
        //і тепер ESP32 виходить на зв'язок, щоб віддати дані та отримати вказівки.
        [Fact]
        public async Task Sync_WhenPlantExists_SavesTelemetryAndClearsPendingCommand()
        {
            // Arrange
            using var context = GetInMemoryDbContext();
            // Створюємо тестову рослину з командою на примусовий полив
            var plant = new Plant { Id = 1, Name = "Ficus", HasPendingWaterCommand = true, MinMoistureThreshold = 30 };
            context.Plants.Add(plant);
            await context.SaveChangesAsync();

            var controller = new DeviceController(context, null);
            var telemetry = new Telemetry { PlantId = 1, SoilMoisture = 45 };

            // Act
            var result = await controller.Sync(telemetry) as OkObjectResult;

            // Assert
            Assert.NotNull(result); // Статус 200 OK

            // Перевіряємо, чи збереглася телеметрія в базі
            Assert.Equal(1, context.Telemetries.Count());

            // Перевіряємо, чи скринька команд очистилася (False)
            var dbPlant = await context.Plants.FindAsync(1);
            Assert.NotNull(dbPlant);
            Assert.False(dbPlant.HasPendingWaterCommand);
        }

        // ================= ТЕСТИ ДЛЯ AppController =================
        //Що буде, якщо мобільний додаток надішле команду "Полити рослину №99"
        [Fact]
        public async Task ForceWater_WhenPlantNotFound_ReturnsNotFound()
        {
            // Arrange
            using var context = GetInMemoryDbContext();
            var controller = new AppController(context, null);

            // Act
            var result = await controller.ForceWater(99); // ID 99 не існує

            // Assert
            Assert.IsType<NotFoundObjectResult>(result);
        }
        //Користувач відкрив додаток, побачив свій кактус і натиснув кнопку "Полити"
        [Fact]
        public async Task ForceWater_WhenPlantExists_SetsPendingCommandFlag()
        {
            // Arrange
            using var context = GetInMemoryDbContext();
            var plant = new Plant { Id = 1, Name = "Cactus", HasPendingWaterCommand = false };
            context.Plants.Add(plant);
            await context.SaveChangesAsync();

            var controller = new AppController(context, null);

            // Act
            var result = await controller.ForceWater(1) as OkObjectResult;

            // Assert
            Assert.NotNull(result);

            // Перевіряємо, чи змінився прапорець у базі даних
            var dbPlant = await context.Plants.FindAsync(1);
            Assert.NotNull(dbPlant); // Заспокоюємо компілятор, гарантуючи, що рослина знайдена
            Assert.True(dbPlant.HasPendingWaterCommand);
        }
        //Перевіряємо зміну налаштувань
        [Fact]
        public async Task UpdateConfig_WhenPlantExists_UpdatesModeAndThreshold()
        {
            // Arrange: створюємо рослину зі старими налаштуваннями
            using var context = GetInMemoryDbContext();
            context.Plants.Add(new Plant { Id = 1, ControlMode = 2, MinMoistureThreshold = 20 });
            await context.SaveChangesAsync();

            var controller = new AppController(context, null);
            // Нові налаштування від мобільного додатка: Режим 3 (Фікс. поріг) і вологість 40%
            var config = new UpdateConfigDto { ControlMode = 3, ManualThreshold = 40 };

            // Act: викликаємо метод оновлення
            var result = await controller.UpdateConfig(1, config) as OkObjectResult;

            // Assert: перевіряємо, чи змінилися дані в базі
            Assert.NotNull(result);
            var dbPlant = await context.Plants.FindAsync(1);
            Assert.NotNull(dbPlant);
            Assert.Equal(3, dbPlant.ControlMode); // Перевіряємо новий режим
            Assert.Equal(40, dbPlant.MinMoistureThreshold); // Перевіряємо новий поріг
        }
    }
}