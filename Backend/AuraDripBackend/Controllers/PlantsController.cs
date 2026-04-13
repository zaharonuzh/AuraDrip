using AuraDripBackend.Data;
using AuraDripBackend.Models;

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

using PostHog;

namespace AuraDripBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")] // /api/plants
    public class PlantsController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly IPostHogClient _posthog;

        public PlantsController(AppDbContext context, IPostHogClient posthog)
        {
            _context = context;
            _posthog = posthog;
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