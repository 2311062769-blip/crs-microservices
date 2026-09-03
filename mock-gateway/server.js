const express = require('express');
const cors = require('cors');
const crypto = require('crypto');
const app = express();
app.use(cors());
app.use(express.json());

let courses = Array.from({ length: 23 }, (_, i) => ({
  id: i + 1,
  tenMonHoc: `Mon hoc so ${i + 1}`,
  soTinChi: (i % 4) + 2,
  soChoConLai: i % 5 === 0 ? 0 : 10 - (i % 10),
  soChoToiDa: 30,
}));
let nextCourseId = 24;

const USERS = {
  admin: { id: 1, password: 'admin123', role: 'ADMIN', token: 'fake-admin-token-123' },
  student1: { id: 2, password: 'student123', role: 'STUDENT', token: 'fake-student-token-456' },
};

let registrations = [];
let nextRegId = 1;

function findUserByToken(token) {
  return Object.entries(USERS).find(([, u]) => u.token === token);
}

function requireAuth(req, res, next) {
  const auth = req.headers.authorization || '';
  const token = auth.startsWith('Bearer ') ? auth.slice(7) : null;
  const found = token && findUserByToken(token);
  if (!found) {
    return res.status(401).json({ message: 'Ban khong co quyen thuc hien thao tac nay' });
  }
  req.user = { id: found[1].id, username: found[0], role: found[1].role };
  next();
}

function requireAdmin(req, res, next) {
  if (req.user.role !== 'ADMIN') {
    return res.status(403).json({ message: 'Chi ADMIN moi duoc thuc hien thao tac nay' });
  }
  next();
}

app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body || {};
  const user = USERS[username];
  if (!user || user.password !== password) {
    return res.status(401).json({ message: 'Sai username hoac password' });
  }
  res.json({ userId: user.id, token: user.token, username, role: user.role });
});

// COURSES
app.get('/api/courses', (req, res) => {
  const keyword = (req.query.keyword || '').toString().toLowerCase();
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 10;
  const filtered = courses.filter((c) => c.tenMonHoc.toLowerCase().includes(keyword));
  const totalPages = Math.ceil(filtered.length / size) || 0;
  const content = filtered.slice(page * size, page * size + size);
  res.json({ content, totalPages, totalElements: filtered.length, page, size });
});

app.get('/api/courses/:id', (req, res) => {
  const course = courses.find((c) => c.id === Number(req.params.id));
  if (!course) return res.status(404).json({ message: 'Khong tim thay mon hoc' });
  res.json(course);
});

app.post('/api/courses', requireAuth, requireAdmin, (req, res) => {
  const { tenMonHoc, soTinChi, soChoToiDa } = req.body || {};
  if (courses.some((c) => c.tenMonHoc.toLowerCase() === String(tenMonHoc).toLowerCase())) {
    return res.status(400).json({ message: 'Ten mon hoc da ton tai' });
  }
  const newCourse = {
    id: nextCourseId++,
    tenMonHoc,
    soTinChi: Number(soTinChi),
    soChoToiDa: Number(soChoToiDa),
    soChoConLai: Number(soChoToiDa),
  };
  courses.push(newCourse);
  res.status(201).json(newCourse);
});

app.put('/api/courses/:id', requireAuth, requireAdmin, (req, res) => {
  const id = Number(req.params.id);
  const course = courses.find((c) => c.id === id);
  if (!course) return res.status(404).json({ message: 'Khong tim thay mon hoc' });
  const { tenMonHoc, soTinChi, soChoToiDa } = req.body || {};
  const trung = courses.some(
    (c) => c.id !== id && c.tenMonHoc.toLowerCase() === String(tenMonHoc).toLowerCase()
  );
  if (trung) return res.status(400).json({ message: 'Ten mon hoc da ton tai' });
  course.tenMonHoc = tenMonHoc;
  course.soTinChi = Number(soTinChi);
  course.soChoToiDa = Number(soChoToiDa);
  res.json(course);
});

app.delete('/api/courses/:id', requireAuth, requireAdmin, (req, res) => {
  const id = Number(req.params.id);
  const before = courses.length;
  courses = courses.filter((c) => c.id !== id);
  if (courses.length === before) return res.status(404).json({ message: 'Khong tim thay mon hoc' });
  res.status(204).send();
});

// REGISTRATIONS
app.get('/api/registrations/my', requireAuth, (req, res) => {
  const myRegs = registrations.filter(
    (r) => r.studentId === req.user.id && r.trangThai === 'DA_DANG_KY'
  );
  res.json(myRegs);
});

app.post('/api/registrations', requireAuth, (req, res) => {
  const { courseId } = req.body || {};
  const course = courses.find((c) => c.id === Number(courseId));
  if (!course) return res.status(404).json({ message: 'Khong tim thay mon hoc' });

  const daDangKy = registrations.some(
    (r) => r.studentId === req.user.id && r.courseId === Number(courseId) && r.trangThai === 'DA_DANG_KY'
  );
  if (daDangKy) {
    return res.status(400).json({ message: 'Sinh vien da dang ky mon hoc nay roi' });
  }

  if (course.soChoConLai <= 0) {
    return res.status(400).json({ message: 'Mon hoc da het cho' });
  }

  course.soChoConLai -= 1;
  const newReg = {
    id: nextRegId++,
    studentId: req.user.id,
    courseId: Number(courseId),
    trangThai: 'DA_DANG_KY',
    ngayDangKy: new Date().toISOString(),
  };
  registrations.push(newReg);
  res.status(201).json(newReg);
});

app.delete('/api/registrations/:id', requireAuth, (req, res) => {
  const id = Number(req.params.id);
  const reg = registrations.find((r) => r.id === id && r.studentId === req.user.id);
  if (!reg) return res.status(404).json({ message: 'Khong tim thay dang ky' });

  reg.trangThai = 'DA_HUY';
  const course = courses.find((c) => c.id === reg.courseId);
  if (course) course.soChoConLai += 1;

  res.status(204).send();
});

// ==================== QUAN LY API KEY ====================
let apiKeys = [];
let nextApiKeyId = 1;

function generateApiKey() {
  return 'crs_' + crypto.randomBytes(24).toString('base64url');
}

function isValidForScope(keyValue, requiredScope) {
  const key = apiKeys.find((k) => k.keyValue === keyValue);
  if (!key) return false;
  if (key.status !== 'ACTIVE') return false;
  if (key.expiresAt && new Date(key.expiresAt) < new Date()) return false;
  return key.scopes.split(',').map((s) => s.trim()).includes(requiredScope);
}

function requireApiKey(requiredScope) {
  return (req, res, next) => {
    const apiKey = req.headers['x-api-key'];
    if (!apiKey || !isValidForScope(apiKey, requiredScope)) {
      return res.status(403).json({ message: 'API Key khong hop le hoac khong du quyen' });
    }
    next();
  };
}

// CRUD quan tri, chi ADMIN
app.get('/api/api-keys', requireAuth, requireAdmin, (req, res) => {
  res.json(apiKeys);
});

app.post('/api/api-keys', requireAuth, requireAdmin, (req, res) => {
  const { ownerName, scopes, validDays } = req.body || {};
  if (!ownerName || !scopes) {
    return res.status(400).json({ message: 'Ten doi tac va scopes khong duoc de trong' });
  }
  const newKey = {
    id: nextApiKeyId++,
    keyValue: generateApiKey(),
    ownerName,
    scopes,
    status: 'ACTIVE',
    expiresAt: validDays ? new Date(Date.now() + Number(validDays) * 86400000).toISOString() : null,
    createdAt: new Date().toISOString(),
  };
  apiKeys.push(newKey);
  res.status(201).json(newKey);
});

app.delete('/api/api-keys/:id', requireAuth, requireAdmin, (req, res) => {
  const id = Number(req.params.id);
  const key = apiKeys.find((k) => k.id === id);
  if (!key) return res.status(404).json({ message: 'Khong tim thay API Key' });
  key.status = 'REVOKED';
  res.status(204).send();
});

// Route doi tac vi du, bao ve boi API Key (scope courses:read)
app.get('/api/public/courses', requireApiKey('courses:read'), (req, res) => {
  res.json(courses);
});
// ==================== HET PHAN API KEY ====================

const PORT = 8080;
app.listen(PORT, () => console.log(`Mock gateway running at http://localhost:${PORT}`));
