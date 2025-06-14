-- Insert users
INSERT INTO users (id, full_name, email, username, biography, picture, role_type) VALUES
(1, 'Anna Martínez López', 'anna.martinez@estudiant.upf.edu', 'annaml', 'Biomedical engineering student curious about brain-computer interfaces.', 'annaml.png', 'STUDENT'),
(2, 'Lucas Ferrer Grau', 'lucas.ferrer@estudiant.upf.edu', 'lucasfg', 'Bachelor student in Computer Engineering, backend developer in the making.', 'lucasfg.png', 'STUDENT'),
(3, 'Marina Soler Puig', 'marina.soler@estudiant.upf.edu', 'marinasp', 'Data science student driven by social impact and open data.', 'marinasp.png', 'STUDENT'),
(4, 'Joan Vidal Riera', 'joan.vidal@estudiant.upf.edu', 'joanvr', 'Lover of audiovisual tech and real-time systems.', 'joanvr.png', 'STUDENT'),
(5, 'Clara Puig Roca', 'clara.puig@estudiant.upf.edu', 'clarapr', 'Telecom engineering student fascinated by wireless networks.', 'clarapr.png', 'STUDENT');

-- Insert student details
INSERT INTO student (student_id, birthdate, social_links, degrees, enrolled_subjects) VALUES
(1, '2001-04-10', 
 '{"twitter": "https://www.twitter.com/annaml", "instagram": "https://www.instagram.com/annaml"}', 
 '{"Bachelor": "Biomedical Engineering"}', 
 '{"10001": "Cognitive Neuroscience", "10002": "Social Psychology"}'),

(2, '2000-11-05', 
 '{"github": "https://www.github.com/lucasfg", "linkedin": "https://www.linkedin.com/in/lucasfg"}', 
 '{"Bachelor": "Computer Engineering"}', 
 '{"20001": "Artificial Intelligence", "20002": "Advanced Programming"}'),

(3, '2002-03-18', 
 '{"instagram": "https://www.instagram.com/marinasp"}', 
 '{"Bachelor": "Mathematical Engineering in Data Science"}', 
 '{"30001": "Political Theory", "30002": "International Relations"}'),

(4, '2001-09-12', 
 '{"github": "https://www.github.com/joanvr", "twitter": "https://www.twitter.com/joanvr"}', 
 '{"Bachelor": "Computational Audiovisual Engineering"}', 
 '{"40001": "Data Structures", "40002": "Operating Systems"}'),

(5, '2000-01-22', 
 '{"goodreads": "https://www.goodreads.com/clarapr"}', 
 '{"Bachelor": "Telecommunications Network Engineering"}', 
 '{"50001": "Wireless Communications", "50002": "Signal Processing"}');
 
 
 -- Insert ENTITY users
INSERT INTO users (id, full_name, email, username, biography, picture, role_type) VALUES
(6, 'Secretary UPF', 'secretary@upf.edu', 'secretaryupf', 'Official academic secretariat of UPF. Handling student records and administrative tasks.', 'secretaryupf.png', 'ENTITY'),
(7, 'UPF Library', 'library@upf.edu', 'libraryupf', 'Providing academic resources, study spaces, and digital databases to students and staff.', 'libraryupf.png', 'ENTITY'),
(8, 'Faculty of ICT', 'ictfaculty@upf.edu', 'ictfacultyupf', 'Faculty of Information and Communication Technologies. Promoting research and innovation.', 'ictfacultyupf.png', 'ENTITY'),
(9, 'UPF Language Services', 'languages@upf.edu', 'languagesupf', 'Helping students with language learning and official certifications.', 'languagesupf.png', 'ENTITY'),
(10, 'Career Services', 'careers@upf.edu', 'careersupf', 'Supporting students with internships, job offers, and professional growth.', 'careersupf.png', 'ENTITY');

-- Insert ENTITY details
INSERT INTO entity (entity_id, department) VALUES
(6, 'Academic Secretariat'),
(7, 'University Library'),
(8, 'Information and Communication Technologies'),
(9, 'Language Support'),
(10, 'Career Development and Internships');


-- Insert ASSOCIATION users
INSERT INTO users (id, full_name, email, username, biography, picture, role_type) VALUES
(11, 'Trempats Association', 'trempats.association@gmail.com', 'trempats', 'Student hiking and nature club promoting outdoor activities.', 'trempats.png', 'ASSOCIATION'),
(12, 'EHUB UPF', 'ehub.upf@gmail.com', 'ehub', 'Entrepreneurship hub fostering startups and innovation on campus.', 'ehub.png', 'ASSOCIATION'),
(13, 'Actium UPF', 'actium.upf@gmail.com', 'actium', 'Cultural and artistic activities association at UPF.', 'actium.png', 'ASSOCIATION'),
(14, 'AEMMAR UPF', 'aemmar.upf@gmail.com', 'aemmar', 'Association of Economics and Management students at UPF.', 'aemmar.png', 'ASSOCIATION'),
(15, 'UPF Volunteer Group', 'upf.volunteers@gmail.com', 'upfvolunteers', 'Organizing volunteering activities and social impact projects.', 'upfvolunteers.png', 'ASSOCIATION');

-- Insert association details
INSERT INTO association (association_id, verification_status, verification_date) VALUES
(11, 'APPROVED', NOW()),
(12, 'APPROVED', NOW()),
(13, 'APPROVED', NOW()),
(14, 'APPROVED', NOW()),
(15, 'APPROVED', NOW());

-- Posts for STUDENTS (user_id 1-5)
INSERT INTO post (source_id, user_id, image, content) VALUES
(NULL, 1, NULL, 'Excited to start my final project on brain-computer interfaces!'),
(1,    2, NULL, 'Good luck, Anna! Let me know if you need any help.'),

(NULL, 3, '3.png', 'Attended a fascinating lecture on open data policies today.'),
(3,    4, NULL, 'Sounds interesting! I would like to join next time.'),

(NULL, 5, '5.png', 'Exploring new wireless protocols for 5G networks.'),
(5,    1, NULL, 'Can you share some resources?');
-- post_id = 1, 2, 3, 4, 5, 6

-- Posts for ENTITIES (user_id 6-10)
INSERT INTO post (source_id, user_id, image, content) VALUES
(NULL, 6, NULL, 'Remember to submit your course registration by the end of the week.'), 
(7,    7, '8.png', 'Thanks for the reminder! Also, library hours extended during exams.'), 
(NULL, 8, NULL, 'Join our seminar on 5G technologies next Thursday.'),                   
(9,    9, NULL, 'Is registration required?');                                          
-- post_id = 7, 8, 9, 10

-- Posts for ASSOCIATIONS (user_id 11-15)
INSERT INTO post (source_id, user_id, image, content) VALUES
(NULL, 11, '11.png', 'Join our weekend hike in the Pyrenees! All levels welcome.'),           
(11,   12, NULL, 'Looking forward to it!'),                                          
(NULL, 13, NULL, 'Art exhibition opening next Friday at the campus gallery.'),          
(13,   14, NULL, 'Will there be guided tours?'),                                         
(NULL, 15, NULL, 'Volunteer with us to help the local community.'),                     
(15,   4, '16.png', 'Great initiative! Here are photos from last volunteering event.'); 
-- post_id = 11, 12, 13, 14, 15, 16