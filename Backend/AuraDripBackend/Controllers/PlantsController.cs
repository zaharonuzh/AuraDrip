using AuraDripBackend.Data;
using AuraDripBackend.Models;

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace AuraDripBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")] // /api/plants
    public class PlantsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public PlantsController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost]
        public async Task<IActionResult> AddPlant([FromBody] Plant newPlant)
        {
            _context.Add(newPlant);

            await _context.SaveChangesAsync();

            return Ok(newPlant);
        }
    }
}