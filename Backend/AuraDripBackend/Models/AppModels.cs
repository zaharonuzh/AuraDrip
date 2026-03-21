using System.ComponentModel.DataAnnotations;

namespace AuraDripBackend.Models
{
    public class Plant
    {
        public int Id { get; set; }
        [Required]
        public string Name { get; set; } = string.Empty;
        public string Species { get; set; } = string.Empty;
        public DateTime DatePlanted { get; set; }
        public int MinMoistureThreshold { get; set; }
        // Режими: 1 - Ручний, 2 - Розумне Авто, 3 - Фіксоване Авто
        public int ControlMode { get; set; } = 2;
        public bool HasPendingWaterCommand { get; set; } = false;
        // Навігаційні властивості (EF Core зробить зв'язки автоматично)
        public List<Telemetry> Telemetries { get; set; } = new();
        public List<WateringEvent> WateringEvents { get; set; } = new();
    }

    public class Telemetry
    {
        public int Id { get; set; }
        public int PlantId { get; set; } // Зовнішній ключ
        public DateTime Timestamp { get; set; }
        public int SoilMoisture { get; set; }
        public double AirHumidity { get; set; }
        public double AirTemperature { get; set; }
    }

    public class WateringEvent
    {
        public int Id { get; set; }
        public int PlantId { get; set; }
        public DateTime StartTime { get; set; }
        public int DurationSeconds { get; set; }
        public bool ManualTrigger { get; set; }
    }

    public class DailyReport
    {
        public int Id { get; set; }
        public int PlantId { get; set; }
        public DateTime ReportDate { get; set; }
        public double AvgMoisture { get; set; }
        public int MinMoisture { get; set; }
        public double MaxTemp { get; set; }
        public int WateringCount { get; set; }
    }

    public class PlantCatalog
    {
        public int Id { get; set; }
        // Назва виду (наприклад: "Еуфорбія", "Фікус Бенджаміна", "Кактус")
        public string SpeciesName { get; set; } = string.Empty;
        // Короткий опис для додатка (щоб користувачу було цікаво читати)<якщо не треба буде, то я приберу>
        public string Description { get; set; } = string.Empty;

        // Рекомендована вологість для цього виду (з якою рослина народиться)
        public int DefaultMoistureThreshold { get; set; }

        // Тут можна додати посилання на іконку/фотографію для мобільного додатка<якщо не треба буде, то я приберу>
        public string ImageUrl { get; set; } = string.Empty;
    }

    // Налаштування
    public class UpdateConfigDto
    {
        // Наприклад: 1 - Ручний, 2 - Авто (Розумний), 3 - Авто (Фіксований поріг)
        public int ControlMode { get; set; }
        public int? ManualThreshold { get; set; } // int? означає, що може бути порожнім (null)
    }
}