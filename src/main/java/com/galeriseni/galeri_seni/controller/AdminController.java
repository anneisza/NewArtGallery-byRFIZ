// src/main/java/com/galeriseni/galeri_seni/controller/AdminController.java
package com.galeriseni.galeri_seni.controller;

import com.galeriseni.galeri_seni.entity.*;
import com.galeriseni.galeri_seni.repository.UserRepository;
import com.galeriseni.galeri_seni.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ArtworkService artworkService;
    private final ArtistService artistService;
    private final ExhibitionService exhibitionService;
    private final CurationService curationService;
    private final AiAttributionService aiAttributionService;
    private final UserService userService;
    private final GalleryInfoService galleryInfoService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User tidak ditemukan"));
    }

    // ====== DASHBOARD ======
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("totalArtworks", artworkService.countAll());
        model.addAttribute("totalArtists", artistService.countAll());
        model.addAttribute("totalExhibitions", exhibitionService.countAll());
        model.addAttribute("totalKurators", userService.countKurators());
        model.addAttribute("totalPending", artworkService.countPending());
        model.addAttribute("artworks", artworkService.findAll());
        return "admin/dashboard"; // 🔹 Mengarah ke src/main/resources/templates/admin/dashboard.html
    }

    // ====== KARYA SENI ======
    @GetMapping("/karya-seni")
    public String karyaSeni(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("artworks", artworkService.findAll());
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("exhibitions", exhibitionService.findAll());
        return "admin/karya-seni"; // 🔹 Mengarah ke src/main/resources/templates/admin/karya-seni.html
    }

    @PostMapping("/karya-seni/tambah")
    public String tambahKarya(@RequestParam String title,
                              @RequestParam Long artistId,
                              @RequestParam(required = false) Long exhibitionId,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Integer yearCreated,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "imageUrl", required = false) String imageUrl,
                              RedirectAttributes redirectAttr) throws IOException {
        // File upload prioritas utama, URL sebagai fallback
        String finalImageUrl = saveFile(imageFile, "artworks");
        if (finalImageUrl == null && imageUrl != null && !imageUrl.isBlank()) {
            finalImageUrl = imageUrl;
        }
        artworkService.create(title, artistId, exhibitionId, description, yearCreated, finalImageUrl);
        redirectAttr.addFlashAttribute("successMessage", "Karya seni berhasil ditambahkan.");
        return "redirect:/admin/karya-seni";
    }

    @PostMapping("/karya-seni/edit/{id}")
    public String editKarya(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam Long artistId,
                            @RequestParam(value = "exhibitionId", required = false) Long exhibitionId,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "yearCreated", required = false) Integer yearCreated,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            @RequestParam(value = "imageUrl", required = false) String imageUrl,
                            RedirectAttributes redirectAttr) throws IOException {
        // File upload prioritas utama, URL sebagai fallback
        String finalImageUrl = saveFile(imageFile, "artworks");
        if (finalImageUrl == null && imageUrl != null && !imageUrl.isBlank()) {
            finalImageUrl = imageUrl;
        }
        artworkService.update(id, title, artistId, exhibitionId, description, yearCreated, finalImageUrl);
        redirectAttr.addFlashAttribute("successMessage", "Karya seni berhasil diperbarui.");
        return "redirect:/admin/karya-seni";
    }

    @PostMapping("/karya-seni/hapus/{id}")
    public String hapusKarya(@PathVariable Long id, RedirectAttributes redirectAttr) {
        artworkService.delete(id);
        redirectAttr.addFlashAttribute("successMessage", "Karya seni berhasil dihapus.");
        return "redirect:/admin/karya-seni";
    }

    // ====== SENIMAN ======
    @GetMapping("/seniman")
    public String seniman(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("artists", artistService.findAll());
        return "admin/seniman"; // 🔹 Mengarah ke src/main/resources/templates/admin/seniman.html
    }

    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // 1. Simpan fisik file langsung ke dalam folder resources/static/assets/
        String uploadDir = "src/main/resources/static/assets/" + subfolder + "/";
        Files.createDirectories(Paths.get(uploadDir));

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.write(Paths.get(uploadDir + filename), file.getBytes());

        // 2. URL yang disimpan ke database menggunakan /assets/...
        return "/assets/" + subfolder + "/" + filename;
    }

    @PostMapping("/seniman/tambah")
    public String tambahSeniman(@RequestParam String name,
                                @RequestParam(required = false) String specialty,
                                @RequestParam(required = false) String bio,
                                @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                                RedirectAttributes redirectAttr) throws IOException {

        // Langsung simpan file (jika ada)
        String finalPhotoUrl = saveFile(photoFile, "artists");

        artistService.create(name, specialty, bio, finalPhotoUrl);
        redirectAttr.addFlashAttribute("successMessage", "Seniman berhasil ditambahkan.");
        return "redirect:/admin/seniman";
    }

    @PostMapping("/seniman/edit/{id}")
    public String editSeniman(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String specialty,
                              @RequestParam(required = false) String bio,
                              @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                              RedirectAttributes redirectAttr) throws IOException {

        // 1. Ambil nama file baru jika ada file yang diupload lewat form
        String finalPhotoUrl = saveFile(photoFile, "artists");

        // 2. Jika tidak ada file baru yang diupload (null), cari data lama di database
        if (finalPhotoUrl == null) {
            // Menggunakan .orElse(null) untuk mengatasi masalah "Incompatible types Found Optional"
            Artist existingArtist = artistService.findById(id).orElse(null);

            if (existingArtist != null) {
                // Menggunakan .getPhoto() sesuai dengan properti di entity Artist kamu
                finalPhotoUrl = existingArtist.getPhoto();
            }
        }

        artistService.update(id, name, specialty, bio, finalPhotoUrl);
        redirectAttr.addFlashAttribute("successMessage", "Seniman berhasil diperbarui.");
        return "redirect:/admin/seniman";
    }

    @PostMapping("/seniman/hapus/{id}")
    public String hapusSeniman(@PathVariable Long id, RedirectAttributes redirectAttr) {
        artistService.delete(id);
        redirectAttr.addFlashAttribute("successMessage", "Seniman berhasil dihapus.");
        return "redirect:/admin/seniman";
    }

    // ====== PAMERAN ======
    @GetMapping("/pameran")
    public String pameran(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("exhibitions", exhibitionService.findAll());
        return "admin/pameran"; // 🔹 Mengarah ke src/main/resources/templates/admin/pameran.html
    }

    @PostMapping("/pameran/tambah")
    public String tambahPameran(@RequestParam String title,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate,
                                @RequestParam("status") String status,
                                RedirectAttributes redirectAttr) {

        LocalDate start = (startDate != null && !startDate.trim().isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.trim().isEmpty()) ? LocalDate.parse(endDate) : null;
        Exhibition.Status exhibitionStatus = Exhibition.Status.valueOf(status);

        exhibitionService.create(title, description, location, start, end, exhibitionStatus);
        redirectAttr.addFlashAttribute("successMessage", "Pameran berhasil ditambahkan.");
        return "redirect:/admin/pameran";
    }

    @PostMapping("/pameran/edit/{id}")
    public String editPameran(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam(value = "description", required = false) String description,
                              @RequestParam(value = "location", required = false) String location,
                              @RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam("status") String status,
                              RedirectAttributes redirectAttr) {

        LocalDate start = (startDate != null && !startDate.trim().isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.trim().isEmpty()) ? LocalDate.parse(endDate) : null;
        Exhibition.Status exhibitionStatus = Exhibition.Status.valueOf(status);

        exhibitionService.update(id, title, description, location, start, end, exhibitionStatus);

        redirectAttr.addFlashAttribute("successMessage", "Pameran berhasil diperbarui.");
        return "redirect:/admin/pameran";
    }

    @PostMapping("/pameran/hapus/{id}")
    public String hapusPameran(@PathVariable Long id, RedirectAttributes redirectAttr) {
        exhibitionService.delete(id);
        redirectAttr.addFlashAttribute("successMessage", "Pameran berhasil dihapus.");
        return "redirect:/admin/pameran";
    }

    // ====== INFO GALERI ======
    @GetMapping("/info-galeri")
    public String infoGaleri(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        galleryInfoService.getInfo().ifPresent(info -> model.addAttribute("galleryInfo", info));
        return "admin/info-galeri";
    }

    @PostMapping("/info-galeri/simpan")
    public String simpanInfoGaleri(@RequestParam(required = false) Long id,
                                   @RequestParam String galleryName,
                                   @RequestParam(required = false) String tagline,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String address,
                                   @RequestParam(required = false) String openingHours,
                                   @RequestParam(required = false) String contactEmail,
                                   @RequestParam(required = false) Integer foundedYear,
                                   RedirectAttributes redirectAttr) {
        GalleryInfo info = galleryInfoService.getInfo().orElse(new GalleryInfo());
        info.setGalleryName(galleryName);
        info.setTagline(tagline);
        info.setDescription(description);
        info.setAddress(address);
        info.setOpeningHours(openingHours);
        info.setContactEmail(contactEmail);
        info.setFoundedYear(foundedYear);
        galleryInfoService.save(info);
        redirectAttr.addFlashAttribute("successMessage", "Info galeri berhasil diperbarui.");
        return "redirect:/admin/info-galeri";
    }

    // ====== AKUN KURATOR ======
    @GetMapping("/akun-kurator")
    public String akunKurator(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("kurators", userService.findAllKurators());
        return "admin/akun-kurator";
    }

    @PostMapping("/akun-kurator/tambah")
    public String tambahKurator(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String password,
                                RedirectAttributes redirectAttr) {
        try {
            userService.createKurator(name, email, password);
            redirectAttr.addFlashAttribute("successMessage", "Akun kurator berhasil dibuat.");
        } catch (IllegalArgumentException e) {
            redirectAttr.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/akun-kurator";
    }

    @PostMapping("/akun-kurator/edit/{id}")
    public String editKurator(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String email,
                              @RequestParam(required = false) String password,
                              RedirectAttributes redirectAttr) {
        userService.updateKurator(id, name, email, password);
        redirectAttr.addFlashAttribute("successMessage", "Akun kurator berhasil diperbarui.");
        return "redirect:/admin/akun-kurator";
    }

    @PostMapping("/akun-kurator/hapus/{id}")
    public String hapusKurator(@PathVariable Long id, RedirectAttributes redirectAttr) {
        userService.deleteKurator(id);
        redirectAttr.addFlashAttribute("successMessage", "Akun kurator berhasil dihapus.");
        return "redirect:/admin/akun-kurator";
    }

    // ====== BYPASS KURASI ======
    @GetMapping("/bypass-kurasi")
    public String bypassKurasi(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("artworks", artworkService.findAll());
        model.addAttribute("curations", curationService.findAll());
        model.addAttribute("kurators", userService.findAllKurators());
        return "admin/bypass-kurasi";
    }

    @PostMapping("/bypass-kurasi/simpan")
    public String simpanBypassKurasi(@RequestParam Long artworkId,
                                     @RequestParam String notes,
                                     @RequestParam Artwork.CurationStatus status,
                                     Authentication auth,
                                     RedirectAttributes redirectAttr) {
        User admin = getCurrentUser(auth);
        curationService.saveCuration(artworkId, admin.getId(), notes, status);
        redirectAttr.addFlashAttribute("successMessage", "Bypass kurasi berhasil disimpan.");
        return "redirect:/admin/bypass-kurasi";
    }

    @PostMapping("/bypass-kurasi/hapus/{id}")
    public String hapusBypassKurasi(@PathVariable Long id, RedirectAttributes redirectAttr) {
        curationService.delete(id);
        redirectAttr.addFlashAttribute("successMessage", "Data kurasi berhasil dihapus.");
        return "redirect:/admin/bypass-kurasi";
    }

    // ====== BYPASS ATRIBUSI AI ======
    @GetMapping("/bypass-atribusi-ai")
    public String bypassAtribusiAi(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("artworks", artworkService.findAll());

        // UBAH "attributions" MENJADI "aiAttributions" SESUAI DI HTML
        model.addAttribute("aiAttributions", aiAttributionService.findAll());

        return "admin/bypass-atribusi-ai";
    }

    @PostMapping("/bypass-atribusi-ai/simpan")
    public String simpanBypassAtribusi(@RequestParam Long artworkId,
                                       @RequestParam AiAttribution.InvolvementLevel involvementLevel,
                                       @RequestParam(required = false) String softwareUsed,
                                       @RequestParam(required = false) String promptText,
                                       Authentication auth,
                                       RedirectAttributes redirectAttr) {
        User admin = getCurrentUser(auth);
        aiAttributionService.saveAttribution(artworkId, admin.getId(),
                involvementLevel, softwareUsed, promptText);
        redirectAttr.addFlashAttribute("successMessage", "Bypass atribusi AI berhasil disimpan.");
        return "redirect:/admin/bypass-atribusi-ai";
    }

    @PostMapping("/bypass-atribusi-ai/hapus/{id}")
    public String hapusBypassAtribusi(@PathVariable Long id, RedirectAttributes redirectAttr) {
        aiAttributionService.delete(id);
        redirectAttr.addFlashAttribute("successMessage", "Data atribusi AI berhasil dihapus.");
        return "redirect:/admin/bypass-atribusi-ai";
    }
}